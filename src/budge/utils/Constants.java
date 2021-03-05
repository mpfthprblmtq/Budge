package budge.utils;

import java.awt.*;

public class Constants {

    public static final String SPACE = " ";
    public static final String COMMA = ",";
    public static final String DASH = "-";
    public static final String NEWLINE = "\n";

    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";
    
    public static final String ANY = "Any";

    public static int ACCOUNT_COL = 0;
    public static int DATE_COL = 1;
    public static int CHECK_COL = 2;
    public static int DESCRIPTION_COL = 3;
    public static int DEBIT_COL = 4;
    public static int CREDIT_COL = 5;
    public static int STATUS_COL = 6;
    public static int ENDING_BALANCE_COL = 7;

    public static String MERCHANT_CATEGORY_CODE = "Merchant Category Code: ";
    public static String MERCHANT_CATEGORY_CODE_REGEX = ".*Merchant Category Code: \\d{4}";

    public static String CARD = "Card ";
    public static String CARD_REGEX = ".*Card \\d{4}";

    public static String DATE = "Date ";
    public static String DATE_REGEX = ".*Date \\d{2}/\\d{2}/\\d{2}.*";

    public static String ID_REGEX = "(.*) \\d \\d{10} \\d \\d{4}";
    public static String ID_MATCHER_REGEX = "(.*)( \\d \\d{10} \\d \\d{4}?)";

    public static final String DOUBLE_REGEX = "\\d*\\.\\d*";
    public static final String INT_REGEX = "\\d*";

    public static final String REQUIRED = "* Required";
    public static final String INVALID = "* Invalid";

    public static final Color SUCCESS = new Color(34, 140, 34);
    public static final Color ERROR = new Color(140, 34, 34);

}
