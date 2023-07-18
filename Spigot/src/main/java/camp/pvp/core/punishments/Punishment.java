package camp.pvp.core.punishments;

import camp.pvp.core.SpigotCore;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.*;

@Getter @Setter
public class Punishment {

    public enum Type {
        BAN, BLACKLIST, MUTE;

        public String getMessage() {
            switch(this) {
                case BAN:
                    return "&cYour account has been banned from the PvP Camp Network.";
                case BLACKLIST:
                    return "&4Your account is blacklisted from the PvP Camp Network.";
                default:
                    return "&cYour account is muted on the PvP Camp Network.";
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
    }

    private final UUID uuid;
    private Punishment.Type type;
    private UUID issuedTo, issuedFrom, pardoner;
    private Date issued, expires, pardoned;
    private String reason, pardonReason, ip;
    private boolean ipPunished, silent;

    public Punishment(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isActive() {
        if(getPardoned() != null) {
            return false;
        }

        if(getExpires() != null) {
            return getExpires().before(new Date());
        } else {
            return true;
        }
    }

    public void importFromDocument(Document doc) {
        this.type = Punishment.Type.valueOf(doc.getString("type"));
        this.issuedTo = doc.get("issued_to", UUID.class);
        this.issuedFrom = doc.get("issued_from", UUID.class);
        this.pardoner = doc.get("pardoner", UUID.class);
        this.issued = doc.getDate("issued");
        this.expires = doc.getDate("expires");
        this.pardoned = doc.getDate("pardoned");
        this.ip = doc.getString("ip");
        this.reason = doc.getString("reason");
        this.pardonReason = doc.getString("pardon_reason");
        this.ipPunished = doc.getBoolean("ip_punished");
        this.silent = doc.getBoolean("silent");
    }

    public Map<String, Object> exportToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", getType());
        map.put("issued_to", getIssuedTo());
        map.put("issued_from", getIssuedFrom());
        map.put("pardoner", getPardoner());
        map.put("issued", getIssued());
        map.put("expires", getExpires());
        map.put("pardoned", getPardoned());
        map.put("ip", getIp());
        map.put("reason", getReason());
        map.put("pardon_reason", getPardonReason());
        map.put("ip_punished", isIpPunished());
        map.put("silent", isSilent());

        return map;
    }
}
