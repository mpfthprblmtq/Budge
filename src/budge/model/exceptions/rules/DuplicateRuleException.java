package budge.model.exceptions.rules;

public class DuplicateRuleException extends Exception {
    public DuplicateRuleException(String errorMessage) {
        super(errorMessage);
    }
}
