package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static SimpleDateFormat df = new SimpleDateFormat("HH:mm");
    public static String getDateTime() {
        return df.format(new Date());
    }
}
