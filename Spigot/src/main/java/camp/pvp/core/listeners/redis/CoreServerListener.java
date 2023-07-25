package camp.pvp.core.listeners.redis;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.server.CoreServer;
import camp.pvp.core.server.CoreServerManager;
import camp.pvp.redis.RedisSubscriberListener;
import com.google.gson.JsonObject;

public class CoreServerListener implements RedisSubscriberListener {

    private CoreServerManager csm;

    public CoreServerListener(CoreServerManager csm) {
        this.csm = csm;
    }

    @Override
    public void onReceive(JsonObject json) {
        String name = json.get("name").getAsString();
        CoreServer server = csm.findServer(name);

        if(server == null) {
            server = new CoreServer(name);
            csm.getCoreServers().add(server);
        }

        server.setType(json.get("type").getAsString());
        server.setOnline(json.get("online").getAsInt());
        server.setSlots(json.get("slots").getAsInt());
        server.setCurrentlyOnline(json.get("currently_online").getAsBoolean());
        server.setLastUpdate(json.get("last_update").getAsLong());

        if(!server.isCurrentlyOnline()) {
            csm.getCoreServers().remove(server);
        }
    }
}
