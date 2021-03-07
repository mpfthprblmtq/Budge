package budge.model.exceptions.savings_entry;

public class SavingsEntryNotFoundException extends Exception {
    public SavingsEntryNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
