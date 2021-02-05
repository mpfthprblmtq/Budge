package budge.model.exceptions.entry;

public class DuplicateEntryException extends Exception {
    public DuplicateEntryException(String errorMessage) {
        super(errorMessage);
    }
}
