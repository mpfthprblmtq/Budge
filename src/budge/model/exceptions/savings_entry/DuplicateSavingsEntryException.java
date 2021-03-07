package budge.model.exceptions.savings_entry;

public class DuplicateSavingsEntryException extends Exception {
    public DuplicateSavingsEntryException(String errorMessage) {
        super(errorMessage);
    }
}
