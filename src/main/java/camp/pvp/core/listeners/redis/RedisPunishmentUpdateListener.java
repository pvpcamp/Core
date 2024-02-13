package camp.pvp.core.listeners.redis;

import camp.pvp.core.Core;
import camp.pvp.redis.RedisSubscriberListener;
import com.google.gson.JsonObject;

import java.util.UUID;

public class RedisPunishmentUpdateListener implements RedisSubscriberListener {

    private final Core plugin;
    public RedisPunishmentUpdateListener(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onReceive(JsonObject jsonObject) {
        UUID uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
        String fromServer = jsonObject.get("from_server").getAsString();
        boolean deleted = jsonObject.get("deleted").getAsBoolean();

        if(fromServer.equals(plugin.getCoreServerManager().getCoreServer().getName())) {
            return;
        }

        if(deleted) {
            plugin.getPunishmentManager().getLoadedPunishments().remove(uuid);
        } else {
            plugin.getPunishmentManager().importOneAsync(uuid);
        }
    }
}
