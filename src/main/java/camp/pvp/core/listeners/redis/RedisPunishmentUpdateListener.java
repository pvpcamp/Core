package camp.pvp.core.listeners.redis;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.redis.RedisSubscriberListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.UUID;

public class RedisPunishmentUpdateListener implements RedisSubscriberListener {

    private final Core plugin;
    public RedisPunishmentUpdateListener(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onReceive(JsonObject jsonObject) {
        UUID uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
        UUID target = UUID.fromString(jsonObject.get("issued_to").getAsString());
        JsonArray ips = jsonObject.get("ips").getAsJsonArray();
        String fromServer = jsonObject.get("from_server").getAsString();
        boolean deleted = jsonObject.get("deleted").getAsBoolean();

        if(fromServer.equals(plugin.getCoreServerManager().getCoreServer().getName())) {
            return;
        }

        if(deleted) {
            plugin.getPunishmentManager().getLoadedPunishments().remove(uuid);
            return;
        }

        for(CoreProfile profile : plugin.getCoreProfileManager().getLoadedProfiles().values()) {
            boolean containsIp = false;
            for(JsonElement element : ips) {
                if(profile.getIpList().contains(element.getAsString())) {
                    containsIp = true;
                    break;
                }
            }

            if(profile.getUuid().equals(target) || containsIp) {
                plugin.getPunishmentManager().importOneAsync(uuid);
                return;
            }
        }
    }
}
