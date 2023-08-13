package camp.pvp.core.profiles;

import camp.pvp.core.Core;
import camp.pvp.core.ranks.Rank;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter @Setter
public class Grant implements Comparable<Grant>{

    public Grant(UUID uuid) {
        this.uuid = uuid;
    }
    public enum Type {
        ADDED, REMOVED;


        @Override
        public String toString() {
            switch(this) {
                case ADDED:
                    return "&6Added Rank";
                default:
                    return "&cRemoved Rank";
            }
        }

        public ItemStack getIcon() {
            switch(this) {
                case ADDED:
                    return new ItemStack(Material.EMERALD);
                default:
                    return new ItemStack(Material.REDSTONE);
            }
        }
    }

    private final UUID uuid;
    private Date date;
    private UUID issuedTo, issuedFrom;
    private String issuedToName, issuedFromName;
    private Rank rank;
    private Type type;
    private boolean deleted;

    public void importFromDocument(Core plugin, Document doc) {
        this.date = doc.getDate("date");
        this.issuedTo = doc.get("issued_to", UUID.class);
        this.issuedFrom = doc.get("issued_from", UUID.class);
        this.issuedToName = doc.getString("issued_to_name");
        this.issuedFromName = doc.getString("issued_from_name");
        Rank rank = plugin.getRankManager().getRanks().get(doc.get("rank", UUID.class));
        if(rank != null) {
            this.rank = plugin.getRankManager().getRanks().get(doc.get("rank", UUID.class));
        } else {
            this.rank = null;
        }
        this.type = Type.valueOf(doc.getString("type"));
    }

    public Map<String, Object> exportToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("date", getDate());
        map.put("issued_to", getIssuedTo());
        map.put("issued_from", getIssuedFrom());
        map.put("issued_to_name", getIssuedToName());
        map.put("issued_from_name", getIssuedFromName());
        map.put("rank", rank.getUuid());
        map.put("type", type.name());

        return map;
    }

    @Override
    public int compareTo(Grant grant) {
        return grant.getDate().compareTo(this.getDate());
    }
}
