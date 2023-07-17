package camp.pvp.core.punishments;

import camp.pvp.core.SpigotCore;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class PunishmentManager {

    private SpigotCore plugin;
    private Map<UUID, Punishment> loadedPunishments;
    public PunishmentManager(SpigotCore plugin) {
        this.plugin = plugin;
        this.loadedPunishments = new HashMap<>();

        plugin.getLogger().info("Started PunishmentManager.");
    }
}
