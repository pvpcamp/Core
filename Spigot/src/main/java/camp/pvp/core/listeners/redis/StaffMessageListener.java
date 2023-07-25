package camp.pvp.core.listeners.redis;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import camp.pvp.redis.RedisSubscriberListener;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StaffMessageListener implements RedisSubscriberListener {
    @Override
    public void onReceive(JsonObject json) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission("core.staff")) {
                player.sendMessage(Colors.get(json.get("message").getAsString()));
            }
        }
    }
}
