package camp.pvp.core.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static String getDifference(Date date1, Date date2) {
        long duration = date1.getTime() - date2.getTime();
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;

        return days + " day(s), " + hours + " hour(s), " + minutes + " minute(s), and " + seconds + " second(s).";
    }

}
