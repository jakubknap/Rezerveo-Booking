package pl.rezerveo.booking.common.constant;

public class Patterns {

    public static final String PERSON_NAME_PATTERN = "^(?!\\s)[^0-9!#|$^`\\\\~_%&\\\"'(){}*+,./:;<=>?@\t\n\\]\\[]*(?<!\\s)$";
    public static final String PASSWORD_PATTERN = "^(?=.*\\p{Ll})(?=.*\\p{Lu})(?=.*\\d)(?=.*[!@#$%^&*(?.\\-_])[\\p{L}\\d!@#$%^&*(?.\\-_]{8,}$";
    public static final String SAVE_TEXT_PATTERN = "^[\\p{L}\\p{N} .,!?:;'\"()@&%\\-_]+$";
}