package camp.pvp.core.punishments;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter @Setter
public class Punishment implements Comparable<Punishment>{

    public enum Type {
        BLACKLIST, BAN, MUTE, WARNING;

        @Override
        public String toString() {
            String name = this.name();
            name = name.replace("_", " ");
            return WordUtils.capitalizeFully(name);
        }

        public ChatColor getColor() {
            switch(this) {
                case BAN:
                    return ChatColor.RED;
                case BLACKLIST:
                    return ChatColor.DARK_RED;
                case MUTE:
                    return ChatColor.GOLD;
                default:
                    return ChatColor.GRAY;
            }
        }

        public String getMessage() {
            switch(this) {
                case BAN:
                    return "&cYour account is banned from the PvP Camp Network.";
                case BLACKLIST:
                    return "&4Your account is blacklisted from the PvP Camp Network.";
                case WARNING:
                    return "&cYou have received a warning.";
                default:
                    return "&cYour account is muted across the PvP Camp Network.";
            }
        }

        public String getAppealMessage() {
            switch(this) {
                case BLACKLIST:
                    return "&cThis type of punishment cannot be appealed.";
                default:
                    return "&cIf you would like to appeal your punishment, join our Discord: discord.pvp.camp";
            }
        }

        public ItemStack getIcon() {
            ItemStack item = new ItemStack(Material.WOOL);
            switch(this) {
                case BAN:
                    item.setDurability((short) 14);
                    break;
                case BLACKLIST:
                    item.setDurability((short) 15);
                    break;
                case MUTE:
                    item.setDurability((short) 1);
                    break;
                default:
                    item.setDurability((short) 8);
                    break;
            }

            return item;
        }
    }

    private final UUID uuid;
    private Punishment.Type type;
    private UUID issuedTo, issuedFrom, pardoner;
    private Date issued, expires, pardoned, lastUpdate;
    private String issuedToName, issuedFromName, pardonerName, reason, pardonReason;
    private List<String> ips;
    private boolean ipPunished, silent;

    public Punishment(UUID uuid) {
        this.uuid = uuid;
        this.ips = new ArrayList<>();
        this.lastUpdate = new Date();
    }

    public boolean isActive() {
        if(getPardonReason() != null) {
            return false;
        }

        if(getExpires() != null) {
            return new Date().before(getExpires());
        } else {
            return true;
        }
    }

    public void importFromDocument(Document doc) {
        this.type = Punishment.Type.valueOf(doc.getString("type"));
        this.issuedTo = doc.get("issued_to", UUID.class);
        this.issuedFrom = doc.get("issued_from", UUID.class);
        this.pardoner = doc.get("pardoner", UUID.class);
        this.issuedToName = doc.getString("issued_to_name");
        this.issuedFromName = doc.getString("issued_from_name");
        this.pardonerName = doc.getString("pardoner_name");
        this.issued = doc.getDate("issued");
        this.expires = doc.getDate("expires");
        this.pardoned = doc.getDate("pardoned");
        this.ips = doc.getList("ips", String.class);
        this.reason = doc.getString("reason");
        this.pardonReason = doc.getString("pardon_reason");
        this.ipPunished = doc.getBoolean("ip_punished");
        this.silent = doc.getBoolean("silent");
        this.lastUpdate = doc.get("last_update", new Date());
    }

    public Map<String, Object> exportToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", getType());
        map.put("issued_to", getIssuedTo());
        map.put("issued_from", getIssuedFrom());
        map.put("pardoner", getPardoner());
        map.put("issued_to_name", getIssuedToName());
        map.put("issued_from_name", getIssuedFromName());
        map.put("pardoner_name", getPardonerName());
        map.put("issued", getIssued());
        map.put("expires", getExpires());
        map.put("pardoned", getPardoned());
        map.put("ips", getIps());
        map.put("reason", getReason());
        map.put("pardon_reason", getPardonReason());
        map.put("ip_punished", isIpPunished());
        map.put("silent", isSilent());
        map.put("last_update", getLastUpdate());

        return map;
    }

    @Override
    public int compareTo(Punishment punishment) {
        return punishment.getIssued().compareTo(this.getIssued());
    }
}
