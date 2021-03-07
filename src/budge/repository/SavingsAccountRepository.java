package budge.repository;

import budge.Main;
import budge.model.SavingsEntry;
import budge.model.exceptions.savings_entry.DuplicateSavingsEntryException;
import budge.model.exceptions.savings_entry.SavingsEntryNotFoundException;
import budge.utils.Constants;
import budge.utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SavingsAccountRepository {

    // globals
    List<SavingsEntry> savingsAccountEntries;
    File file = new File("src/resources/repository/savings-entries.csv");

    /**
     * Reads in all the SavingsEntry objects currently in the store
     */
    public SavingsAccountRepository() {
        readInSavingsEntries();
    }

    /**
     * Reads in all the SavingsEntry objects currently in the store
     */
    public void readInSavingsEntries() {
        savingsAccountEntries = new ArrayList<>();
        String line;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(Constants.COMMA, -1);
                savingsAccountEntries.add(new SavingsEntry(data));
            }
        } catch (FileNotFoundException e) {
            Main.getFrame().updateConsole(file.getName() + " wasn't found!");
        } catch (IOException e) {
            Main.getFrame().updateConsole("IOException when opening " + file.getName() + " to read!");
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException | NullPointerException e) {
                Main.getFrame().updateConsole("Couldn't close the reader stream for some reason!");
            }
        }
    }

    /**
     * Writes out all of the SavingsEntries currently in the ivar list
     * @throws IOException
     */
    private void writeOutSavingsEntries() throws IOException {
        // sort by date first
        savingsAccountEntries.sort(Comparator.comparing(SavingsEntry::getDate));

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (SavingsEntry entry : this.savingsAccountEntries) {
                writer.write(entry.toString());
                writer.write(Constants.NEWLINE);
            }
            writer.close();
        } catch (IOException e) {
            throw new IOException("IOException while trying to write out to rules file!  " + e.getMessage());
        }
    }

    /**
     * Returns the list of all SavingsObjects
     * @return the list of all SavingsObjects
     */
    public List<SavingsEntry> getSavingsAccountEntries() {
        return this.savingsAccountEntries;
    }

    /**
     * Adds a SavingsEntry object to the store
     * @param entry, the entry to add
     * @throws DuplicateSavingsEntryException
     * @throws IOException
     */
    public void add(SavingsEntry entry) throws DuplicateSavingsEntryException, IOException {
        // check if it already exists
        for (SavingsEntry entryInList : getSavingsAccountEntries()) {
            if (entry.equals(entryInList)) {
                throw new DuplicateSavingsEntryException("Savings Entry already exists!");
            }
        }

        // add it to the rules global then sort it
        savingsAccountEntries.add(entry);
        savingsAccountEntries.sort(Comparator.comparing(SavingsEntry::getDate));

        // add it to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (SavingsEntry entryInList : getSavingsAccountEntries()) {
                writer.append(entryInList.toString());
                writer.append(Constants.NEWLINE);
            }
        } catch (IOException e) {
            throw new IOException("IOException while trying to read " + file.getName() + "!  " + e.getMessage());
        }
    }

    /**
     * Deletes an SavingsEntry object
     * @param entry, the object to delete
     * @throws SavingsEntryNotFoundException
     * @throws IOException
     */
    public void delete(SavingsEntry entry) throws SavingsEntryNotFoundException, IOException {
        // remove the entry
        if (!savingsAccountEntries.remove(entry)) {
            throw new SavingsEntryNotFoundException("Savings Entry with the date " + Utils.formatDate(entry.getDate()) +
                    " and amount " + entry.getAmount() + " was not found!");
        }

        // output the new savings entry array to the file
        try {
            writeOutSavingsEntries();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Updates an SavingsEntry object
     * @param initialEntry, the original SavingsEntry object to search for and update
     * @param newEntry, the SavingsEntry object with the new values
     * @throws SavingsEntryNotFoundException
     * @throws IOException
     */
    public void update(SavingsEntry initialEntry, SavingsEntry newEntry)
            throws SavingsEntryNotFoundException, IOException {
        String toReplace = initialEntry.toString();
        String replaceWith = newEntry.toString();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder inputBuffer = new StringBuilder();
            String line;

            // store the contents of the file in the input buffer
            while ((line = reader.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append("\n");
            }
            reader.close();

            // do the replace
            String fileContents = inputBuffer.toString();
            String newFileContents = fileContents.replace(toReplace, replaceWith);

            // check to see if that rule was even found
            if (fileContents.equals(newFileContents)) {
                throw new SavingsEntryNotFoundException("Savings Entry with the date " + Utils.formatDate(initialEntry.getDate()) +
                        " and amount " + initialEntry.getAmount() + " was not found!");
            }

            // output the new contents to the file
            FileWriter writer = new FileWriter(file);
            writer.append(newFileContents);
            writer.close();

            // set the old rule to the new rule in the rules list
            for (int i = 0; i < savingsAccountEntries.size(); i++) {
                if (savingsAccountEntries.get(i).equals(initialEntry)) {
                    savingsAccountEntries.set(i, newEntry);
                }
            }

        } catch (IOException e) {
            throw new IOException("IOException while editing a saving entry!  " + e.getMessage());
        }
    }

}
