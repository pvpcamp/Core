package camp.pvp.core.ranks;

import camp.pvp.core.SpigotCore;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.*;

@Getter @Setter
public class Rank implements Comparable<Rank>{

    private final UUID uuid;
    private String name, displayName, prefix, color;
    private int weight;
    private boolean defaultRank, nameMcAward;
    private Map<String, List<String>> permissions;
    private List<UUID> parents;

    public Rank(UUID uuid) {
        this.uuid = uuid;
        this.name = "default";
        this.displayName = "Default";
        this.color = "&a";
        this.weight = 0;

        this.permissions = new HashMap<>();
        this.parents = new ArrayList<>();
    }

    public List<Rank> getParents(SpigotCore plugin) {
        List<Rank> ranks = new ArrayList<>();
        RankManager rankManager = plugin.getRankManager();
        for(UUID uuid : getParents()) {
            Rank rank = rankManager.getRanks().get(uuid);
            if(rank != null) {
                ranks.add(rank);
            }
        }

        return ranks;
    }

    public void importFromDocument(SpigotCore plugin, Document doc) {
        this.name = doc.getString("name");
        this.displayName = doc.getString("display_name");
        this.color = doc.getString("color");
        this.weight = doc.getInteger("weight");
        this.prefix = doc.getString("prefix");
        this.defaultRank = doc.getBoolean("default");
        this.nameMcAward = doc.getBoolean("namemc_reward");
        this.permissions = (Map<String, List<String>>) doc.get("permissions");
        this.parents = doc.getList("parents", UUID.class);
    }

    public Map<String, Object> exportToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", getName());
        map.put("display_name", getDisplayName());
        map.put("color", getColor());
        map.put("prefix", getPrefix());
        map.put("weight", getWeight());
        map.put("default", isDefaultRank());
        map.put("namemc_reward", isNameMcAward());
        map.put("permissions", getPermissions());
        map.put("parents", getParents());

        return map;
    }

    @Override
    public int compareTo(Rank r) {
        return r.getWeight() - this.getWeight();
    }
}
