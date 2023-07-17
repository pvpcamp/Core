package camp.pvp.core.punishments;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter @Setter
public class Punishment {

    public enum Type {
        BAN, BLACKLIST, MUTE;
    }

    private final UUID uuid;
    private final Type type;
    private UUID issedTo, issedFrom;
    private Date issued, expires;
    private String reason;

    public Punishment(UUID uuid, Type type) {
        this.uuid = uuid;
        this.type = type;
    }
}
