package budge.service;

import budge.Main;
import budge.model.Category;
import budge.model.Entry;
import budge.model.ParsedEntry;
import budge.utils.Constants;
import budge.utils.StringUtils;

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
            BufferedReader reader = null;

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

    private String parseEntries() {
        for (Entry entry : entries) {
            ParsedEntry parsedEntry = new ParsedEntry(entry);
            rulesService.applyRule(parsedEntry);
            if (parsedEntry.getCategory() == Category.TRANSFER) {
                parseTransfer(parsedEntry);
            }
            accountService.matchAccount(parsedEntry);
            parsedEntries.add(parsedEntry);
        }
        return null;
    }

    public void parseTransfer(ParsedEntry entry) {
        // TODO this
        System.out.println(entry.getDescription());
    }
    
}
