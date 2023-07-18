package camp.pvp.core.listeners.player;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.UUID;

public class PlayerJoinLeaveListeners implements Listener {

    private SpigotCore plugin;
    public PlayerJoinLeaveListeners(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = plugin.getCoreProfileManager().find(player.getUniqueId(), true);

        if(profile == null) {
            profile = plugin.getCoreProfileManager().create(player);
        }

        if(profile.getRanks().size() == 0) {
            profile.getRanks().add(plugin.getRankManager().getDefaultRank());
        }

        plugin.getCoreProfileManager().updatePermissions(profile);

        List<String> welcomeMessages = plugin.getConfig().getStringList("messages.welcome");
        for(String s : welcomeMessages) {
            player.sendMessage(Colors.get(s));
        }

        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = plugin.getCoreProfileManager().find(player.getUniqueId(), true);

        if(profile != null) {
            plugin.getCoreProfileManager().exportToDatabase(profile, true, false);
        }

        plugin.getCoreProfileManager().getPermissionAttachments().remove(player.getUniqueId());

        event.setQuitMessage(null);
    }
}
