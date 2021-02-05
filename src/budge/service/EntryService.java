package budge.service;

import budge.model.Entry;
import budge.model.EntryKey;
import budge.model.ParsedEntry;
import budge.repository.EntryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntryService {

    EntryRepository entryRepository;

    public EntryService() {
        entryRepository = new EntryRepository();
    }

    public void saveAllEntries(List<ParsedEntry> entries) {
        entryRepository.saveAllEntries(entries);
    }

    public List<ParsedEntry> getAllParsedEntries() {
        Map<EntryKey, ParsedEntry> entryMap = entryRepository.getAllEntries();
        return new ArrayList<>(entryMap.values());
    }

    public ParsedEntry getEntryByKey(EntryKey key) {
        return entryRepository.findSingleEntry(key);
    }
}
