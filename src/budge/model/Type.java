package budge.model;

public enum Type {

    WITHDRAWAL ("Withdrawal"),
    DEPOSIT ("Deposit"),
    RECURRING_WITHDRAWAL ("Recurring Withdrawal"),
    WITHDRAWAL_ADJUSTMENT ("Withdrawal Adjustment");

    private final String type;

    Type(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
    
    public static Type fromString(String s) {
        for (Type t : Type.values()) {
            if (t.getType().equalsIgnoreCase(s)) {
                return t;
            }
        }
        return null;
    }
}
