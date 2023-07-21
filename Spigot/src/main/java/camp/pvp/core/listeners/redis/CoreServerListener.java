package camp.pvp.core.listeners.redis;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.server.CoreServer;
import camp.pvp.redis.RedisSubscriberListener;
import com.google.gson.JsonObject;

public class CoreServerListener extends RedisSubscriberListener {

    private SpigotCore plugin;

    public CoreServerListener(SpigotCore plugin) {
        super("core_server_updates");
        this.plugin = plugin;
        plugin.getNetworkHelper().getRedisSubscriber().getListeners().add(this);
    }

    @Override
    public void onReceive(JsonObject json) {
        String name = json.get("name").getAsString();
        CoreServer server = plugin.getCoreServerManager().findServer(name);

        if(server == null) {
            server = new CoreServer(name);
            plugin.getCoreServerManager().getCoreServers().add(server);
        }

        server.setType(json.get("type").getAsString());
        server.setOnline(json.get("online").getAsInt());
        server.setSlots(json.get("slots").getAsInt());
        server.setCurrentlyOnline(json.get("currently_online").getAsBoolean());
        server.setLastUpdate(json.get("last_update").getAsLong());

        if(!server.isCurrentlyOnline()) {
            plugin.getCoreServerManager().getCoreServers().remove(server);
        }
    }
}
