package camp.pvp.core.server;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class CoreServer {

    private final String name;
    private String type;
    private int online, slots;
    private long lastUpdate;
    private boolean currentlyOnline, mutedChat;
    private Long upTime;

    public CoreServer(String name) {
        this.name = name;
        this.currentlyOnline = true;
    }

    public CoreServer(String name, String type) {
        this.name = name;
        this.type = type;
        this.currentlyOnline = true;
        this.mutedChat = false;
    }
}
