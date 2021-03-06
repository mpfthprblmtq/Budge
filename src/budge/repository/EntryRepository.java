package budge.repository;

import budge.model.Entry;
import budge.model.EntryKey;
import budge.model.ParsedEntry;
import budge.model.exceptions.entry.EntryNotFoundException;
import budge.utils.Constants;
import budge.utils.StringUtils;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class EntryRepository {

    // globals
    Map<EntryKey, ParsedEntry> entries;
    File file = new File("src/resources/repository/entries.csv");

    /**
     * Default constructor, initializes the entries global and reads in all the entries
     */
    public EntryRepository() {
        entries = new HashMap<>();
        readInEntries();
    }

    /**
     * Returns the entry map (keys included)
     * @return the map of entries
     */
    public Map<EntryKey, ParsedEntry> getEntryMap() {
        return this.entries;
    }

    /**
     * Returns the list of parsed entries (no keys)
     * @return the list of parsed entries
     */
    public List<ParsedEntry> getEntries() {
        return new ArrayList<>(entries.values());
    }

    /**
     * Reads in the entries from the file and stores them in the entries global
     */
    private void readInEntries() {
        entries = new HashMap<>();
        String line;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                ParsedEntry entry = new ParsedEntry(data);
                EntryKey entryKey = new EntryKey(entry);
                entry.setKey(entryKey);
                entries.put(entryKey, entry);
            }
        } catch (FileNotFoundException e) {
            System.err.println(file.getName() + " wasn't found!");
        } catch (IOException e) {
            System.err.println("IOException when opening " + file.getName() + " to read!");
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException | NullPointerException e) {
                System.err.println("Couldn't close the reader stream for some reason!");
            }
        }
    }

    /**
     * Writes the entries global to the file
     * @throws IOException
     */
    public void writeOutEntries() throws IOException {

        // sort by date first
        List<ParsedEntry> parsedEntries = getEntries();
        parsedEntries.sort(Comparator.comparing(ParsedEntry::getDate));

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (ParsedEntry entry : parsedEntries) {
                writer.write(entry.toString());
                writer.write(Constants.NEWLINE);
            }
            writer.close();
        } catch (IOException e) {
            throw new IOException("IOException while trying to write out to entries file!  " + e.getMessage());
        }
    }

    /**
     * Clears all the entries
     */
    public void clearAllEntries() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(StringUtils.EMPTY);
            writer.close();
            entries.clear();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Gets the entry from the entry map from the key
     * @param key the key to search by
     * @return the entry found, or null if the entry wasn't found
     */
    public ParsedEntry findSingleEntry(EntryKey key) {
        if (entries.containsKey(key)) {
            return entries.get(key);
        }
        return null;
    }

    /**
     * Gets the entries from the entry map from the keys
     * @param keys the list of keys
     */
    public List<ParsedEntry> findMultipleEntries(List<EntryKey> keys) {
        List<ParsedEntry> foundEntries = new ArrayList<>();
        for (EntryKey key : keys) {
            ParsedEntry entry = findSingleEntry(key);
            if (entry != null) {
                foundEntries.add(entry);
            }
        }
        return foundEntries;
    }

    /**
     * Adds new parsed entries to the entries map, then writes out to the file
     * @param parsedEntries the entries to add
     */
    public void addParsedEntries(List<ParsedEntry> parsedEntries) throws IOException {
        for (ParsedEntry entry : parsedEntries) {
            EntryKey key = new EntryKey(entry);
            if (!entries.containsKey(key)) {
                entries.put(key, entry);
            }
        }
        try {
            writeOutEntries();
        } catch (IOException e) {
            throw new IOException("IOException while trying to write out to the entries file!  " + e.getMessage());
        }
    }
    
    public void updateEntries(Map<ParsedEntry, ParsedEntry> entriesToUpdate) throws EntryNotFoundException, IOException {

        // extract keyset to form initial entries list
        List<ParsedEntry> initialEntries = new ArrayList<>(entriesToUpdate.keySet());

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

            // do the replace(s)
            List<ParsedEntry> initialEntriesNotFound = new ArrayList<>();
            for (ParsedEntry initialEntry : initialEntries) {
                String toReplace = initialEntry.toString();
                String replaceWith = entriesToUpdate.get(initialEntry).toString();
                String fileContents = inputBuffer.toString();
                String newFileContents = fileContents.replace(toReplace, replaceWith);
                if (fileContents.equals(newFileContents)) {
                    // check to see if the reason why it "wasn't found" was that we're just overwriting the entry
                    // with the same entry
                    if (!toReplace.equals(replaceWith)) {
                        initialEntriesNotFound.add(initialEntry);
                    }
                }
            }

            // check to see if there are any entries not updated
            if (initialEntriesNotFound.size() > 0) {
                throw new EntryNotFoundException(initialEntriesNotFound.size() + " entries not found!");
            }

            // write out the new file contents to the file
            try {
                writeOutEntries();
            } catch (IOException e) {
                throw new IOException("IOException while trying to write out to the entries file!  " + e.getMessage());
            }

        } catch (IOException e) {
            throw new IOException("IOException while trying to update entries!  " + e.getMessage());
        }
    }
    
}
