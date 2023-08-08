package camp.pvp.core.chattags;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class ChatTag implements Comparable<ChatTag>{

    private final UUID uuid;
    private String name, displayName, tag;
    private boolean visible;

    public ChatTag(UUID uuid) {
        this.uuid = uuid;
        this.visible = true;
    }

    public void importFromDocument(Document doc) {
        this.name = doc.getString("name");
        this.displayName = doc.getString("display_name");
        this.tag = doc.getString("tag");
        this.visible = doc.getBoolean("visible");
    }

    public Map<String, Object> exportToMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("display_name", displayName);
        map.put("tag", tag);
        map.put("visible", visible);

        return map;
    }

    @Override
    public int compareTo(ChatTag o) {
        return this.getName().compareTo(o.getName());
    }
}
