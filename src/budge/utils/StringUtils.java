package budge.utils;

public class StringUtils {
    
    public static final String EMPTY = "";

    public static boolean isEmpty(String s) {
        return s == null || s.equals(EMPTY);
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static boolean isValidDouble(String s) {
        return s.matches(Constants.DOUBLE_REGEX) || s.matches(Constants.INT_REGEX);
    }
    
}
