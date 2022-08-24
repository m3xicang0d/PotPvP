package net.frozenorb.potpvp.util.tablist.headutil;

import java.util.Locale;

public class StringUtils {

    public static String before(String value, String a) {
        // Return substring containing all characters before a string.
        int posA = value.toLowerCase(Locale.ROOT).indexOf(a.toLowerCase(Locale.ROOT));
        if (posA == -1) {
            return "";
        }
        return value.substring(0, posA);
    }

    public static String after(String value, String a) {
        // Returns a substring containing all characters after a string.
        int posA = value.toLowerCase(Locale.ROOT).lastIndexOf(a.toLowerCase(Locale.ROOT));
        if (posA == -1) {
            return "";
        }
        int adjustedPosA = posA + a.length();
        if (adjustedPosA >= value.length()) {
            return "";
        }
        return value.substring(adjustedPosA);
    }
}