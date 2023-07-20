package camp.pvp.core.listeners.mongo;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.events.MongoGuiEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MongoGuiListener implements Listener {

    private SpigotCore plugin;
    public MongoGuiListener(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMongoGui(MongoGuiEvent event) {
        Player player = event.getPlayer();
        if(player != null) {
            event.getGui().open(player);
            player.sendMessage(ChatColor.GREEN + "Request completed in " + event.getRequestTime() + " ms.");
        }
    }
}
