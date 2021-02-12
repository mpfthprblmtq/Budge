package budge.model.exceptions.entry;

public class EntryNotFoundException extends Exception {
    public EntryNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
