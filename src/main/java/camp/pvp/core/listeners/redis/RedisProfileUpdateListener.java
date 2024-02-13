package camp.pvp.core.listeners.redis;

import camp.pvp.core.Core;
import camp.pvp.redis.RedisSubscriberListener;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RedisProfileUpdateListener implements RedisSubscriberListener {

    private Core plugin;

    public RedisProfileUpdateListener(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onReceive(JsonObject json) {
        UUID uuid = UUID.fromString(json.get("uuid").getAsString());

        Bukkit.getScheduler().runTaskLater(plugin, ()-> {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null && player.isOnline()) {
                plugin.getCoreProfileManager().findAsync(player.getUniqueId());
            }
        }, 4);
    }
}
