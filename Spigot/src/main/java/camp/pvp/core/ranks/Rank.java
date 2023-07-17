package camp.pvp.core.ranks;

import camp.pvp.core.SpigotCore;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class Rank implements Comparable<Rank>{

    private final UUID uuid;
    private String name, displayName, prefix, color;
    private int weight;
    private boolean defaultRank;
    private Map<String, List<String>> permissions;

    public Rank(UUID uuid) {
        this.uuid = uuid;
        this.name = "default";
        this.displayName = "Default";
        this.color = "&a";
        this.weight = 0;

        this.permissions = new HashMap<>();
    }

    public void importFromDocument(SpigotCore plugin, Document doc) {
        this.name = doc.getString("name");
        this.displayName = doc.getString("display_name");
        this.color = doc.getString("color");
        this.weight = doc.getInteger("weight");
        this.defaultRank = doc.getBoolean("default");

        this.permissions = (Map<String, List<String>>) doc.get("permissions");
    }

    public Map<String, Object> exportToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", getName());
        map.put("display_name", getDisplayName());
        map.put("color", getColor());
        map.put("weight", getWeight());
        map.put("default", isDefaultRank());
        map.put("permissions", getPermissions());

        return map;
    }

    @Override
    public int compareTo(Rank r) {
        return this.getWeight() - r.getWeight();
    }
}
