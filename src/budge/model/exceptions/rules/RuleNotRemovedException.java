package budge.model.exceptions.rules;

public class RuleNotRemovedException extends Exception {
    public RuleNotRemovedException(String errorMessage) {
        super(errorMessage);
    }
}
