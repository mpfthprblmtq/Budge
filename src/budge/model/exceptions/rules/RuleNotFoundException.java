package budge.model.exceptions.rules;

public class RuleNotFoundException extends Exception {
    public RuleNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
