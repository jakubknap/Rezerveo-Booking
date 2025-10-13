package pl.rezerveo.booking.util;

import lombok.experimental.UtilityClass;

import static java.util.Objects.isNull;

@UtilityClass
public class MaskingUtil {

    public static String maskEmail(String input) {
        if (isNull(input) || input.isEmpty()) {
            return null;
        }

        int atIndex = input.indexOf('@');
        if (atIndex <= 0) {
            return "***";
        }

        int visibleChars = Math.min(2, atIndex);

        int maskLength = Math.max(1, atIndex - visibleChars);

        String maskedPart = input.substring(0, visibleChars) + "*".repeat(maskLength);
        return maskedPart + input.substring(atIndex);
    }
}