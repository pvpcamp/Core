package camp.pvp.core.chattags;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class ChatTag {

    private final UUID uuid;
    private String name, displayName, tag;

    public ChatTag(UUID uuid) {
        this.uuid = uuid;
    }

    public void importFromDocument(Document doc) {
        this.name = doc.getString("name");
        this.displayName = doc.getString("display_name");
        this.tag = doc.getString("tag");
    }

    public Map<String, Object> exportToMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("displayName", displayName);
        map.put("tag", tag);

        return map;
    }
}
