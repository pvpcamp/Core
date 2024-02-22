package camp.pvp.core.profiles;

import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Getter
public class MiniProfile {

    private final UUID uuid;
    private final String name, server;
    private final long retrievalTime;

    public MiniProfile(UUID uuid, String name, String server) {
        this.uuid = uuid;
        this.name = name;
        this.server = server;
        this.retrievalTime = System.currentTimeMillis();
    }
}
