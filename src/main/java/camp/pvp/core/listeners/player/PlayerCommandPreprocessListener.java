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

        if(profile == null) {
            player.kickPlayer("There was an issue with your profile, please relog.");
            return;
        }

        String message = event.getMessage();

        if(message.toLowerCase().startsWith("/2fa") || message.toLowerCase().startsWith("/auth")) {
            return;
        }

        if(profile.getAuthKey() != null && !profile.isAuthenticated()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You are not authenticated.");
            return;
        }

        String name = player.getName();

        ChatHistory chatHistory = new ChatHistory(
                UUID.randomUUID(),
                player.getUniqueId(),
                name,
                message,
                plugin.getCoreServerManager().getCoreServer().getName(),
                ChatHistory.Type.COMMAND,
                new Date(),
                false);

        plugin.getCoreProfileManager().exportHistory(chatHistory, true);
    }
}
