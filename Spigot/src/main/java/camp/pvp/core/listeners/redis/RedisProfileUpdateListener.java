package camp.pvp.core.listeners.redis;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.utils.Colors;
import camp.pvp.redis.RedisSubscriberListener;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RedisProfileUpdateListener extends RedisSubscriberListener {

    private SpigotCore plugin;

    public RedisProfileUpdateListener(SpigotCore plugin) {
        super("core_profile_updates");
        this.plugin = plugin;
        plugin.getNetworkHelper().getRedisSubscriber().getListeners().add(this);
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
