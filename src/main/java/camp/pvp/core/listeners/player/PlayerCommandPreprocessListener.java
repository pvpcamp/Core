package camp.pvp.core.listeners.player;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.ChatHistory;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Date;
import java.util.UUID;

public class PlayerCommandPreprocessListener implements Listener {

    private Core plugin;
    public PlayerCommandPreprocessListener(Core plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(profile != null) {

            String message = event.getMessage();
            if(profile.getAuthKey() != null && !profile.isAuthenticated() && message.toLowerCase().startsWith("2fa") || message.toLowerCase().startsWith("auth")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You are not authenticated.");
                return;
            }

            ChatHistory chatHistory = new ChatHistory(
                    UUID.randomUUID(),
                    player.getUniqueId(),
                    player.getName(),
                    message,
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
