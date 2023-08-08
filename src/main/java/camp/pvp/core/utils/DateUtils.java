package camp.pvp.core.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static String getTimeFormat(long duration) {
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;

        StringBuilder sb = new StringBuilder();

        if(days > 0) {
            sb.append(days);
            sb.append(" day");
            if(days != 1) {
                sb.append("s");
            }
        }

        if(hours > 0) {
            if(days != 0) {
                sb.append(" ");
            }

            sb.append(hours);
            sb.append(" hour");
            if(hours != 1) {
                sb.append("s");
            }
        }

        if(minutes > 0) {
            if(days != 0 || hours != 0) {
                sb.append(" ");
            }

            sb.append(minutes);
            sb.append(" minute");
            if(minutes != 1) {
                sb.append("s");
            }
        }

        if(seconds > 0) {
            if(days != 0 || hours != 0 || minutes != 0) {
                sb.append(" ");
            }

            sb.append(seconds);
            sb.append(" second");
            if(seconds != 1) {
                sb.append("s");
            }
        }

        return sb.toString();
    }

    public static String getDifference(Date date1, Date date2) {
        return getTimeFormat(date1.getTime() - date2.getTime());
    }

    public static String getTimeUntil(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, -1);
        return getDifference(date, calendar.getTime());
    }

}
