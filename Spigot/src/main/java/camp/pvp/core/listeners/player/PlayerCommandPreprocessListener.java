package camp.pvp.core.listeners.player;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.ChatHistory;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Date;
import java.util.UUID;

public class PlayerCommandPreprocessListener implements Listener {

    private SpigotCore plugin;
    public PlayerCommandPreprocessListener(SpigotCore plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(profile != null) {
            ChatHistory chatHistory = new ChatHistory(
                    UUID.randomUUID(),
                    player.getUniqueId(),
                    player.getName(),
                    event.getMessage(),
                    plugin.getCoreServerManager().getCoreServer().getName(),
                    ChatHistory.Type.COMMAND,
                    new Date(),
                    false);

            plugin.getCoreProfileManager().exportHistory(chatHistory, true);
        } else {
            player.sendMessage(ChatColor.RED + "Your profile has not been loaded yet.");
            event.setCancelled(true);
        }
    }
}
