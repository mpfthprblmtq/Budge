package budge.service;

import budge.model.Category;
import budge.model.EntryKey;
import budge.model.ParsedEntry;
import budge.model.Type;
import budge.model.exceptions.entry.EntryNotFoundException;
import budge.repository.EntryRepository;
import budge.utils.StringUtils;
import budge.utils.Utils;

import java.io.IOException;
import java.util.*;

public class EntryService {

    // repo
    EntryRepository entryRepository;

    /**
     * Default constructor, just initializes the repo
     */
    public EntryService() {
        entryRepository = new EntryRepository();
    }

    /**
     * Returns the entries global list from the repo
     * @return the entries global list from the repo
     */
    public List<ParsedEntry> getEntries() {
        return entryRepository.getEntries();
    }

    /**
     * Clears all the entries
     */
    public void clearAllEntries() {
        entryRepository.clearAllEntries();
    }

    /**
     * Gets a single entry by key
     * @param key the key to search on
     * @return the entry found, or null if not found
     */
    public ParsedEntry getEntryByKey(EntryKey key) {
        return entryRepository.findSingleEntry(key);
    }

    /**
     * Gets multiple entries by keys
     * @param keys the keys to search on
     * @return the entries found
     */
    public List<ParsedEntry> getEntriesByKeys(List<EntryKey> keys) {
        return entryRepository.findMultipleEntries(keys);
    }

    /**
     * Adds newly parsed entries to the array
     */
    public String addParsedEntries(List<ParsedEntry> entries) {
        try {
            entryRepository.addParsedEntries(entries);
            return null;
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    /**
     * Edits an entry
     */
    public String updateEntries(List<ParsedEntry> initialEntries, List<ParsedEntry> newEntries) {
        Map<ParsedEntry, ParsedEntry> updateEntriesMap = new HashMap<>();
        if (initialEntries.size() == newEntries.size()) {
            for (int i = 0; i < initialEntries.size(); i++) {
                updateEntriesMap.put(initialEntries.get(i), newEntries.get(i));
            }
        } else {
            return "Initial and new entries lists don't match!";
        }

        try {
            entryRepository.updateEntries(updateEntriesMap);
            return null;
        } catch (EntryNotFoundException | IOException e) {
            return e.getMessage();
        }
    }

    /**
     * Filters all entries based on conditions given
     * @param account
     * @param dateFrom
     * @param dateTo
     * @param description
     * @param parsed
     * @param category
     * @return the list of filtered entries
     */
    public List<ParsedEntry> filter(String account, String dateFrom, String dateTo, String description, String type, Boolean parsed, String category) {
        List<ParsedEntry> filteredEntries = new ArrayList<>();
        for (ParsedEntry entry : getEntries()) {

            // account
            boolean accountMatches = entry.getAccount().contains(account);

            // date
            boolean dateMatches = entry.getDate().before(Utils.formatDate(dateTo)) && entry.getDate().after(Utils.formatDate(dateFrom));

            // description
            String descriptionToCheck = entry.getDescription();
            boolean descriptionMatches = StringUtils.contains(descriptionToCheck, description);

            // type
            boolean typeMatches = false;
            if (type.equals("Deposit")) {
                if (entry.getType().equals(Type.DEPOSIT) || entry.getType().equals(Type.WITHDRAWAL_ADJUSTMENT)) {
                    typeMatches = true;
                }
            } else if (type.equals("Withdrawal")) {
                if (entry.getType().equals(Type.WITHDRAWAL) || entry.getType().equals(Type.RECURRING_WITHDRAWAL)) {
                    typeMatches = true;
                }
            } else {
                typeMatches = true;
            }

            // parsed
            boolean parsedMatches;
            if (parsed == null) {
                parsedMatches = true;
            } else {
                parsedMatches = entry.isParsed() == parsed;
            }

            // category
            boolean categoryMatches;
            if (entry.getCategory() == null) {
                categoryMatches = StringUtils.isEmpty(category);
            } else {
                categoryMatches = entry.getCategory().getCategory().contains(category);
            }

            if (accountMatches && dateMatches && descriptionMatches && typeMatches && parsedMatches && categoryMatches) {
                filteredEntries.add(entry);
            }
        }
        return filteredEntries;
    }
}
