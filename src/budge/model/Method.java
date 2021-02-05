package budge.model;

public enum Method {
    DIVIDEND ("Dividend"),
    ACH ("ACH"),
    DEBIT_CARD ("Debit Card"),
    DEBIT_CARD_FEE ("Debit Card Fee"),
    HOME_BANKING_TRANSFER ("Home Banking Transfer");

    private final String method;

    Method(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
    
    public static Method fromString(String s) {
        for (Method m : Method.values()) {
            if (m.getMethod().equalsIgnoreCase(s)) {
                return m;
            }
        }
        return null;
    }
}
