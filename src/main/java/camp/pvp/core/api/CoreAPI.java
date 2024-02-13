package camp.pvp.core.api;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CoreAPI {

    private final Core plugin;
    public CoreAPI(Core plugin) {
        this.plugin = plugin;
    }

    public CoreProfile getLoadedProfile(Player player) {
        return getLoadedProfile(player.getUniqueId());
    }

    public CoreProfile getLoadedProfile(UUID uuid) {
        return plugin.getCoreProfileManager().getLoadedProfile(uuid);
    }

    public CoreProfile getLoadedProfile(String name) {
        return plugin.getCoreProfileManager().getLoadedProfile(name);
    }

    public CompletableFuture<CoreProfile> findAsync(UUID uuid) {
        return plugin.getCoreProfileManager().findAsync(uuid);
    }

    public CompletableFuture<CoreProfile> findAsync(String name) {
        return plugin.getCoreProfileManager().findAsync(name);
    }
}
