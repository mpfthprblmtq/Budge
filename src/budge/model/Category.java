package budge.model;

public enum Category {

    GAS ("Gas"),
    EATING_OUT ("Eating Out"),
    RECORDS ("Records"),
    CONCERTS ("Concerts"),
    ENTERTAINMENT ("Entertainment"),
    GROCERIES ("Groceries"),
    HOME_SUPPLIES ("Home Supplies"),
    LIFE_SUPPLIES ("Life Supplies"),
    GROCERIES_HOME_SUPPLIES ("Groceries/Home Supplies"),
    PET_STUFF ("Pet Stuff"),
    MISCELLANEOUS ("Miscellaneous"),
    UNCATEGORIZED ("Uncategorized"),
    CAR_PAYMENT ("Car Payment"),
    STUDENT_LOAN ("Student Loan"),
    INTEREST ("Interest"),
    TRANSFER ("Transfer"),
    SALARY ("Salary"),
    DEPOSIT ("Deposit"),
    CREDIT_CARD_PAYMENT ("Credit Card Payment"),
    CAR_STUFF ("Car Stuff"),
    VACATION ("Vacation"),
    ATM ("ATM");

    private String category;

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
