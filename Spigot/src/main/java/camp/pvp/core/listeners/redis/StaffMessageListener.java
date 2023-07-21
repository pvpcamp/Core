package camp.pvp.core.listeners.redis;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import camp.pvp.redis.RedisSubscriberListener;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StaffMessageListener extends RedisSubscriberListener {

    private SpigotCore plugin;

    public StaffMessageListener(SpigotCore plugin) {
        super("core_staff");
        this.plugin = plugin;
        plugin.getNetworkHelper().getRedisSubscriber().getListeners().add(this);
    }

    @Override
    public void onReceive(JsonObject json) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
            if(profile != null && profile.isStaffMode()) {
                player.sendMessage(Colors.get(json.get("message").getAsString()));
            }
        }
    }
}
