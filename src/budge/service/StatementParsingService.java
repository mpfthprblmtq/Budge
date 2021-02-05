package budge.service;

import budge.Main;
import budge.model.Category;
import budge.model.Entry;
import budge.model.ParsedEntry;

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

    public void process(List<File> files) {
        readInFiles(files);
        parseEntries();
        entryService.saveAllEntries(parsedEntries);
    }

    private void readInFiles(List<File> files) {
        files.forEach((file) -> {
            String line;
            BufferedReader reader = null;

            try {
                reader = new BufferedReader(new FileReader(file));
                line = reader.readLine();   // skip the first line
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    List<String> dataList = Arrays.asList(data);
                    int iter = dataList.size() - 8;
                    for (int i = 0; i < iter; i++) {
                        dataList = trimOutExtraCommas(dataList);
                    }
                    entries.add(new Entry(dataList));
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
        });
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

    private void parseEntries() {
        for (Entry entry : entries) {
            ParsedEntry parsedEntry = new ParsedEntry(entry);
            rulesService.parseRule(parsedEntry);
            if (parsedEntry.getCategory() == Category.TRANSFER) {
                rulesService.parseTransfer(parsedEntry);
            }
            accountService.matchAccount(parsedEntry);
            parsedEntries.add(parsedEntry);
        }
    }
    
}
