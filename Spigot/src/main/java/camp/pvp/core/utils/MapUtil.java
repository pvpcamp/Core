package camp.pvp.core.utils;

import com.google.common.collect.Maps;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {

    public static Map<String, Object> createMap(Object... objects) {
        Map<String, Object> toReturn = Maps.newHashMap();

        String key = null;
        for (int i = 0; i < objects.length; i++) {
            if ((i + 1) % 2 != 0) {
                key = (String) objects[i];
            } else {
                toReturn.put(key, objects[i]);
            }
        }
        return toReturn;
    }

    public static Map<String, Object> cloneDocument(Document document) {
        Map<String, Object> toReturn = new HashMap<>();

        for (String key : document.keySet()) {
            toReturn.put(key, document.get(key));
        }
        return toReturn;
    }
}