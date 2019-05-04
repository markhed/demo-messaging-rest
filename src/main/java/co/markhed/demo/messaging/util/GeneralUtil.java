package co.markhed.demo.messaging.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralUtil {

    private GeneralUtil() {}

    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String TIME_ZONE = "CET";

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);

    public static String format(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String path(String value) {
        return "/" + value;
    }

    public static String path(int value) {
        return "/" + value;
    }


}
