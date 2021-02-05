package budge.repository;

import budge.model.Entry;
import budge.model.EntryKey;
import budge.model.ParsedEntry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntryRepository {
    
    Map<EntryKey, ParsedEntry> entries;
    
    public EntryRepository() {
        entries = new HashMap<>();
        readInEntries();
    }
    
    private void readInEntries() {
        File file = new File("src/resources/repository/entries.csv");
        entries = new HashMap<>();
        String line;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            line = reader.readLine();   // skip the first line
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
    
    public Map<EntryKey, ParsedEntry> getAllEntries() {
        readInEntries();
        return this.entries;
    }

    public ParsedEntry findSingleEntry(EntryKey key) {
        if (entries.containsKey(key)) {
            return entries.get(key);
        }
        return null;
    }
    
    public void saveAllEntries(List<ParsedEntry> entriesToSave) {
        File file = new File("src/resources/repository/entries.csv");
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(file));
            out.write("Account,Post Date,Check No.,Type,Method,Description,Transaction Date,ID,Debit Card,Merchant Code,Amount,Status,Ending Balance,Category,Notes");
            out.write("\n");
            for (ParsedEntry entry : entriesToSave) {
                out.write(entry + "\n");
            }
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    public void updateEntries(List<Entry> entriesToUpdate) {
        
    }
    
}
