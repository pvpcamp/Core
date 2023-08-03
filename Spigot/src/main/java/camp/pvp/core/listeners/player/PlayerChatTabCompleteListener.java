package camp.pvp.core.listeners.player;

import camp.pvp.core.SpigotCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

import java.util.Collection;

public class PlayerChatTabCompleteListener implements Listener {

    private SpigotCore plugin;
    public PlayerChatTabCompleteListener(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent event) {

        Collection<String> tabCompletions = event.getTabCompletions();

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!tabCompletions.contains(player.getName())) {
                if (player.getName().toLowerCase().startsWith(event.getLastToken().toLowerCase())) {
                    tabCompletions.add(player.getName());
                }
            }
        }
    }
}
