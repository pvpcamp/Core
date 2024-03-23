package camp.pvp.core.server;

import camp.pvp.core.profiles.MiniProfile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class CoreServer {

    private final String name;
    private String type, displayName;
    private List<MiniProfile> players;
    private List<String> motd;
    private Material material;
    private int slots, serverSlot;
    private long lastUpdate, upTime;
    private boolean currentlyOnline, mutedChat, showInServerList, staffOnlyServerList, hub, whitelisted;

    public CoreServer(String name, String type) {
        this.name = name;
        this.type = type;
        this.currentlyOnline = true;
        this.mutedChat = false;
        this.material = Material.BEDROCK;
        this.players = new ArrayList<>();
        this.motd = new ArrayList<>();
    }
}
