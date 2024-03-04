package camp.pvp.core.listeners.redis;

import camp.pvp.core.profiles.MiniProfile;
import camp.pvp.core.server.CoreServer;
import camp.pvp.core.server.CoreServerManager;
import camp.pvp.core.utils.DateUtils;
import camp.pvp.redis.RedisSubscriberListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import java.util.*;

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
            server = new CoreServer(name, json.get("type").getAsString());
            csm.getCoreServers().add(server);

            csm.getPlugin().getCoreProfileManager().staffBroadcast("&cServer &f" + name + " &chas been found.");
        }

        server.setSlots(json.get("slots").getAsInt());
        server.setUpTime(json.get("uptime").getAsLong());
        server.setMutedChat(json.get("muted_chat").getAsBoolean());
        server.setLastUpdate(json.get("last_update").getAsLong());

        List<MiniProfile> players = new ArrayList<>();

        for (JsonElement e : json.getAsJsonArray("players")) {
            players.add(MiniProfile.deserialize(e));
        }

        server.setPlayers(players);

        if(!server.isCurrentlyOnline()) {
            server.setCurrentlyOnline(true);
            csm.getPlugin().getCoreProfileManager().staffBroadcast("&cServer &f" + name + " &csent online status.");
        }
    }
}
