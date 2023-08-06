package camp.pvp.core.profiles;

import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Getter
public class TempProfile {

    private final UUID uuid;
    private final String name, server;
    private Date date;

    public TempProfile(UUID uuid, String name, String server) {
        this.uuid = uuid;
        this.name = name;
        this.server = server;
        this.date = new Date();
    }
}
