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
    private String type, staffList;
    private int online, slots;
    private long lastUpdate, upTime;
    private boolean currentlyOnline, mutedChat;

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
