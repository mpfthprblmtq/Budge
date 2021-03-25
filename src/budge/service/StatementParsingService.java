package budge.service;

import budge.Main;
import budge.model.Category;
import budge.model.Entry;
import budge.model.ParsedEntry;
import budge.model.Type;
import budge.utils.Constants;
import budge.utils.StringUtils;
import budge.utils.Utils;

import java.io.*;
import java.util.*;

public class StatementParsingService {

    private List<Entry> entries;
    private List<ParsedEntry> parsedEntries;

    RulesService rulesService;
    EntryService entryService;
    AccountService accountService;
    
    public StatementParsingService() {

        entries = new ArrayList<>();
        parsedEntries = new ArrayList<>();

        rulesService = Main.getRulesService();
        entryService = Main.getEntryService();
        accountService = Main.getAccountService();
    }

    /**
     * Reads in a list of files and parses through through the resulting entry list
     * @param files the files to read in
     * @return an error message if there's errors, null if all went well
     */
    public String process(List<File> files) {
        // read in the files
        String readResult = readInFiles(files);
        if (StringUtils.isNotEmpty(readResult)) {
            return readResult;
        }

        // parse through the entries
        String parseResult = parseEntries();
        if (StringUtils.isNotEmpty(parseResult)) {
            return parseResult;
        }

        // got here, so all is fine
        entryService.addParsedEntries(parsedEntries);
        return null;
    }

    /**
     * Reads in the list of files ands adds the contents to an entries list
     * @param files the files to read in
     * @return an error message if there's errors, null if all went well
     */
    private String readInFiles(List<File> files) {
        List<String> errors = new ArrayList<>();
        files.forEach((file) -> {
            String line;
            BufferedReader reader;

            try {
                reader = new BufferedReader(new FileReader(file));
                line = reader.readLine();   // skip the first line
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");          // split the line by , delimiter
                    List<String> dataList = Arrays.asList(data);    // convert it to a list
                    int iter = dataList.size() - 8;                 // iterator object to keep track of the elements we want
                    for (int i = 0; i < iter; i++) {
                        dataList = trimOutExtraCommas(dataList);    // trim out extra commas based on the iterator
                    }
                    entries.add(new Entry(dataList));               // create a new Entry and add it to the list
                }
                reader.close();
            } catch (FileNotFoundException e) {
                errors.add(file.getName() + " wasn't found!");
            } catch (IOException e) {
                errors.add("IOException when opening " + file.getName() + " to read!");
            }
        });
        if (!errors.isEmpty()) {
            String error = StringUtils.EMPTY;
            for (String e : errors) {
                error = error.concat(e).concat(Constants.NEWLINE);
            }
            return error;
        }
        return null;
    }

    /**
     * Takes the list of standard Entries, creates a Parsed Entry from each, applies any rules it can on them
     * @return a result of the parsing
     */
    private String parseEntries() {
        for (Entry entry : entries) {
            ParsedEntry parsedEntry = new ParsedEntry(entry);
            rulesService.applyRule(parsedEntry);
            accountService.matchAccount(parsedEntry);
            parsedEntries.add(parsedEntry);
        }
        return null;
    }

    /**
     * Takes in the list of entries we already have and parses them if they need parsing
     * @param entries, the entries list to reprocess
     * @return the number of entries reprocessed
     */
    public int reprocess(List<ParsedEntry> entries) {
        List<ParsedEntry> initialEntries = new ArrayList<>();
        List<ParsedEntry> reprocessedEntries = new ArrayList<>();
        for (ParsedEntry entry : entries) {
            if (!entry.isParsed()) {
                ParsedEntry initialEntry = entry.clone();
                initialEntries.add(initialEntry);

                // try to apply a rule to the entry
                boolean result = rulesService.applyRule(entry);

                // check to see if the rule was applied
                if (result) {
                    // rule was successfully applied, add the new entry to the reprocessed entries list
                    reprocessedEntries.add(entry);
                    accountService.matchAccount(entry);
                } else {
                    // rule wasn't applied, remove the initial entry from the initial entries list
                    initialEntries.remove(initialEntry);
                }
            } else if (entry.getCategory() == Category.TRANSFER && !entry.getDescription().matches(Constants.TRANSFER_REGEX)) {
                List<List<ParsedEntry>> reprocessedTransfers = parseTransfer(entry);
                for (ParsedEntry entryInList : reprocessedTransfers.get(0)) {
                    if (!listContainsEntry(initialEntries, entryInList)) {
                        initialEntries.add(entryInList);
                    }
                }
                for (ParsedEntry entryInList : reprocessedTransfers.get(1)) {
                    if (!listContainsEntry(reprocessedEntries, entryInList)) {
                        reprocessedEntries.add(entryInList);
                    }
                }
            }
        }
        entryService.updateEntries(initialEntries, reprocessedEntries);
        return reprocessedEntries.size();
    }

    /**
     * Utility function to do a deep check if a list contains a parsed entry
     * Checks the type, parsed amount, and date
     * @param entries, the list to check
     * @param entry, the parsed entry to check for
     * @return the result of the check
     */
    private boolean listContainsEntry(List<ParsedEntry> entries, ParsedEntry entry) {
        for (ParsedEntry entryInList : entries) {
            if (entry.getType() == entryInList.getType() &&
                    entry.getParsedAmount().equals(entryInList.getParsedAmount()) &&
                    Utils.formatDateSimple(entry.getDate()).equals(Utils.formatDateSimple(entryInList.getDate()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Utility function that trims out extra commas from the description part of the String data array
     * @param data, the list of strings we're parsing
     * @return, the cleaned list with the clean description
     */
    private List<String> trimOutExtraCommas(List<String> data) {
        List<String> list = new ArrayList<>();
        list.add(data.get(0));
        list.add(data.get(1));
        list.add(data.get(2));
        list.add(data.get(3).concat(" ").concat(data.get(4)));
        for (int i = 5; i < data.size(); i++) {
            list.add(data.get(i));
        }
        return list;
    }

    /**
     * Stupid massive function to look at all of the entries to attempt to match transfers to each other
     * @param entry, the initial entry to check
     * @return a list of lists, one list is the initial entries, the other is the updated entries to then be updated
     */
    public List<List<ParsedEntry>> parseTransfer(ParsedEntry entry) {

        // get all the entries to search from
        List<ParsedEntry> entries = entryService.getEntries();

        // type should be opposite whatever the original entry's one is
        Type type = entry.getType() == Type.WITHDRAWAL ? Type.DEPOSIT : Type.WITHDRAWAL;

        // amount should be the inverse of whatever the original entry's one is
        String amount = entry.getParsedAmount().contains("+") ?
                entry.getParsedAmount().replace("+", "-") :
                entry.getParsedAmount().replace("-", "+");

        // date should be the same
        String date = Utils.formatDateSimple(entry.getDate());

        // lets find the other entry
        boolean found = false;
        ParsedEntry originalEntry1 = null;
        ParsedEntry originalEntry2 = null;
        for (ParsedEntry entryInList : entries) {
            if (entryInList.getType() == type && entryInList.getParsedAmount().equals(amount) &&
                    Utils.formatDateSimple(entryInList.getDate()).equals(date)) {
                originalEntry1 = entry;
                originalEntry2 = entryInList;
                found = true;
            }
        }

        // check if we found it
        if (found) {
            // cleaning the description
            String entry1Description = cleanTransferDescription(originalEntry1.getDescription());
            String entry2Description = cleanTransferDescription(originalEntry2.getDescription());

            // let's get the source and destination of the transfer
            // add them to an original and updated list so we can send them to be updated later
            List<List<ParsedEntry>> entriesToUpdate = Arrays.asList(new ArrayList<>(), new ArrayList<>());
            if (entry1Description.startsWith("To")) {
                entriesToUpdate = getSourceAndDestinationEntries(originalEntry1, originalEntry2, entry1Description, entry2Description);
            } else {
                entriesToUpdate = getSourceAndDestinationEntries(originalEntry2, originalEntry1, entry2Description, entry1Description);
            }

            // grab the source and destination entries to parse further
            ParsedEntry sourceEntry = entriesToUpdate.get(1).get(0);
            ParsedEntry destEntry = entriesToUpdate.get(1).get(1);

            // get rid of all the fluff to leave the memo if there is one
            String sourceDescription = deepCleanTransferDescription(sourceEntry.getDescription());
            String destDescription = deepCleanTransferDescription(destEntry.getDescription());

            // set the description to the formatted one we want (plus the memo if there is one)
            sourceEntry.setDescription(("Transfer to " + destEntry.getAccount() + Constants.SPACE + sourceDescription).trim());
            destEntry.setDescription(("Transfer from " + sourceEntry.getAccount() + Constants.SPACE + destDescription).trim());

            // send them to get updated
            return entriesToUpdate;
        } else {

            // figure out if this entry is the source or destination
            String description = entry.getDescription();
            description = cleanTransferDescription(description);
            description = deepCleanTransferDescription(description);

            // create a new entry and update the description
            ParsedEntry updatedEntry = entry.clone();
            description = "Transfer " + (entry.getParsedAmount().contains("+") ? "from" : "to") + " ??? (Couldn't match transfer) " + description;
            updatedEntry.setDescription(description);

            // send it to get updated
            return Arrays.asList(Collections.singletonList(entry), Collections.singletonList(updatedEntry));
        }
    }

    /**
     * Utility function to break out common logic to get the source/destination entries in a transfer parsing
     * @param originalSource, the original entry designated as the source of the transfer
     * @param originalDestination, the original entry designated as the destination of the transfer
     * @param sourceDescription, the description of the updated source entry to set
     * @param destDescription, the description of the destination source entry to set
     * @return a list of lists, first one is the initial entries, the second one is the updated entries
     */
    private List<List<ParsedEntry>> getSourceAndDestinationEntries(ParsedEntry originalSource, ParsedEntry originalDestination, String sourceDescription, String destDescription) {
        ParsedEntry source = originalSource.clone();
        ParsedEntry destination = originalDestination.clone();
        source.setDescription(sourceDescription);
        destination.setDescription(destDescription);
        List<ParsedEntry> initialEntries = Arrays.asList(originalSource, originalDestination);
        List<ParsedEntry> updatedEntries = Arrays.asList(source, destination);
        return Arrays.asList(initialEntries, updatedEntries);
    }

    /**
     * Initial cleaning description function, should leave the description starting with "From" or "To" so that
     * we can determine source and destination
     * @param description, the string to clean
     * @return a sparkly shiny clean boy
     */
    private String cleanTransferDescription(String description) {
        // get rid of the prefixes and suffixes
        description = description.replace("- -SCU Mobile/", StringUtils.EMPTY);
        description = description.replace("Home Banking Transfer/", StringUtils.EMPTY);
        description = description.replace("/-SCU Mobile", StringUtils.EMPTY);
        description = description.replaceFirst("- ", StringUtils.EMPTY);
        return description;
    }

    /**
     * Scrubs the hell out of that description, hopefully leaving just the memo if there is one
     * @param description, the description to deep clean
     * @return an even more sparkly shiny boy
     */
    private String deepCleanTransferDescription(String description) {
        // get rid of any common words
        description = description.replaceFirst("To", StringUtils.EMPTY);
        description = description.replaceFirst("From", StringUtils.EMPTY);
        description = description.replaceFirst("Share", StringUtils.EMPTY);
        description = description.replaceFirst("0000", StringUtils.EMPTY);
        description = description.replaceFirst("0020", StringUtils.EMPTY);
        description = description.replace("RIPLEY PATRICK A XXXXXXXXXX", StringUtils.EMPTY);
        description = description.replace("RIPLEY PATRICK XXXXXXXXXX", StringUtils.EMPTY);
        description = description.replace("GARDNER AIMEE J XXXXXXXXXX", StringUtils.EMPTY);
        description = description.replace("GARDNER AIMEE XXXXXXXXXX", StringUtils.EMPTY);
        description = description.replace("RIPLEY AIMEE J XXXXXXXXXX", StringUtils.EMPTY);
        description = description.replace("RIPLEY AIMEE XXXXXXXXXX", StringUtils.EMPTY);
        description = description.trim();

        // figure out if we need to return a memo or not
        if (StringUtils.isEmpty(description)) {
            return StringUtils.EMPTY;
        } else {
            return "(".concat(description).concat(")");
        }
    }
    
}
