package budge.model;

public enum Category {

    ATM ("ATM"),
    BANK_FEES ("Bank Fees"),
    CAR_MAINTENANCE ("Car Maintenance"),
    CAR_PAYMENT ("Car Payment"),
    CONCERTS ("Concerts"),
    CREDIT_CARD_PAYMENT ("Credit Card Payment"),
    DEPOSIT ("Deposit"),
    EATING_OUT ("Eating Out"),
    ENTERTAINMENT ("Entertainment"),
    GAS ("Gas"),
    GROCERIES ("Groceries"),
    GROCERIES_HOME_SUPPLIES ("Groceries/Home Supplies"),
    HOME_MAINTENANCE ("Home Maintenance"),
    HOME_SUPPLIES ("Home Supplies"),
    HUMAN_MAINTENANCE ("Human Maintenance"),
    INTEREST ("Interest"),
    MISCELLANEOUS ("Miscellaneous"),
    PET_STUFF ("Pet Stuff"),
    RECORDS ("Records"),
    SALARY ("Salary"),
    STUDENT_LOAN ("Student Loan"),
    TAXES("Taxes"),
    TRANSFER ("Transfer"),
    UNCATEGORIZED ("Uncategorized"),
    UTILITIES_BILLS("Utilities/Bills"),
    VACATION ("Vacation");

    private final String category;

    Category(String category) {
        this.category = category;
    }

    public String getCategory() {
        return this.category;
    }

    public static Category fromString(String s) {
        for (Category c : Category.values()) {
            if (c.getCategory().equalsIgnoreCase(s)) {
                return c;
            }
        }
        return null;
    }
}
