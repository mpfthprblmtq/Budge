package budge.service;

import budge.model.SavingsEntry;
import budge.model.exceptions.savings_entry.DuplicateSavingsEntryException;
import budge.model.exceptions.savings_entry.SavingsEntryNotFoundException;
import budge.repository.SavingsAccountRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SavingsAccountService {

    // globals
    private final SavingsAccountRepository savingsAccountRepository;
    List<SavingsEntry> savingsEntries;

    /**
     * Initializes the repository and gets all the SavingsEntry objects
     */
    public SavingsAccountService() {
        savingsAccountRepository = new SavingsAccountRepository();
        savingsEntries = savingsAccountRepository.getSavingsAccountEntries();
    }

    /**
     * Gets a list of SavingsEntry objects that match the account we hand it in
     * @param account, the account to filter on
     * @return the list of SavingsEntry objects based on the account given
     */
    public List<SavingsEntry> getSavingsEntriesForAccount(String account) {
        return savingsEntries.stream().filter(e -> e.getAccountName().equals(account)).collect(Collectors.toList());
    }

    /**
     * Adds a SavingsEntry object
     * @param entry, the entry to add
     * @return the result of the add, null if success, anything else for error
     */
    public String addSavingsEntry(SavingsEntry entry) {
        try {
            savingsAccountRepository.add(entry);
            return null;
        } catch (DuplicateSavingsEntryException | IOException e) {
            return e.getMessage();
        }
    }

    /**
     * Deletes a SavingsEntry object
     * @param entry, the entry to delete
     * @return the result of the delete, null if success, anything else for error
     */
    public String deleteSavingsEntry(SavingsEntry entry) {
        try {
            savingsAccountRepository.delete(entry);
            return null;
        } catch (SavingsEntryNotFoundException | IOException e) {
            return e.getMessage();
        }
    }

    /**
     * Updates a SavingsEntry object
     * @param initialEntry, the entry to search for that needs updating
     * @param newEntry, the entry with the new values
     * @return the result of the update, null if success, anything else for error
     */
    public String updateSavingsEntry(SavingsEntry initialEntry, SavingsEntry newEntry) {
        try {
            savingsAccountRepository.update(initialEntry, newEntry);
            return null;
        } catch (SavingsEntryNotFoundException | IOException e) {
            return e.getMessage();
        }
    }

}
