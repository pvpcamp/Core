package camp.pvp.core.listeners.redis;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.utils.Colors;
import camp.pvp.redis.RedisSubscriberListener;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RedisProfileUpdateListener implements RedisSubscriberListener {

    private SpigotCore plugin;

    public RedisProfileUpdateListener(SpigotCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onReceive(JsonObject json) {
        UUID uuid = UUID.fromString(json.get("uuid").getAsString());

        Bukkit.getScheduler().runTaskLater(plugin, ()-> {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null && player.isOnline()) {
                plugin.getCoreProfileManager().importFromDatabase(player.getUniqueId(), true);
            }
        }, 4);
    }
}
