package camp.pvp.core.utils;

import java.util.Arrays;
import java.util.List;

public class ChatUtils {

    public static boolean isFiltered(String message) {
        List<String> filteredWords = Arrays.asList(
                "nigger",
                "faggot"
        );

        for(String word : filteredWords) {
            String[] words = message.toLowerCase().split(" ");
            for(String w : words) {
                if(w.matches(".*\\b" + word + "\\b.*")) {
                    return true;
                }
            }
        }

        return false;
    }
}
