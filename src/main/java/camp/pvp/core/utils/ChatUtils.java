package camp.pvp.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    static List<String> filteredWords = Arrays.asList(
            "nigger",
            "niggers",
            "faggot",
            "faggots",
            "beaner",
            "beanes",
            "jew",
            "jews",
            "swat",
            "ddos",
            "dos",
            "dox",
            "mericon"
    );

    static Pattern IP_REGEX = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

    public static boolean isFiltered(String message) {
        String[] words = message.trim().split(" ");
        for(String w : words) {
            Matcher matcher = IP_REGEX.matcher(w);
            if(matcher.matches()) {
                return true;
            }

            w = w.replace("3", "e")
                    .replace("1", "i")
                    .replace("!", "i")
                    .replace("@", "a")
                    .replace("7", "t")
                    .replace("0", "o")
                    .replace("5", "s")
                    .replace("8", "b")
                    .replaceAll("\\p{Punct}|\\d", "").trim();
            for(String filteredWord : filteredWords) {
                if(w.matches("(?i)" + filteredWord)) {
                    return true;
                }
            }
        }

        return false;
    }
}
