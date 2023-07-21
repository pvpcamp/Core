package camp.pvp.core.server;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.listeners.redis.CoreServerListener;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter @Setter
public class CoreServerManager {

    private SpigotCore plugin;
    private CoreServer coreServer;
    private List<CoreServer> coreServers;
    private BukkitTask coreServerUpdater, currentServerUpdater;

    public CoreServerManager(SpigotCore plugin) {
        this.plugin = plugin;
        this.coreServers = new ArrayList<>();

        this.coreServer = new CoreServer(plugin.getConfig().getString("server.name"), plugin.getConfig().getString("server.type"));

        new CoreServerListener(plugin);

        this.coreServerUpdater = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {

            }
        }, 0, 100);

        this.coreServerUpdater = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                coreServer.setOnline(Bukkit.getOnlinePlayers().size());
                coreServer.setSlots(plugin.getServer().getMaxPlayers());
                coreServer.setLastUpdate(new Date().getTime());

                JsonObject json = new JsonObject();
                json.addProperty("name", coreServer.getName());
                json.addProperty("type", coreServer.getType());
                json.addProperty("online", coreServer.getOnline());
                json.addProperty("slots", coreServer.getSlots());
                json.addProperty("currently_online", coreServer.isCurrentlyOnline());
                json.addProperty("last_update", coreServer.getLastUpdate());

                plugin.getNetworkHelper().getRedisPublisher().publishMessage("core_server_updates", json);
            }
        }, 0, 40);

        this.sendStaffMessage("&c&l[Server Updater] &r&cServer &f" + coreServer.getName() + "&c is now online.");
    }

    public CoreServer findServer(String s) {
        for(CoreServer server: plugin.getCoreServerManager().getCoreServers()) {
            if(server.getName().equalsIgnoreCase(s)) {
                return server;
            }
        }

        return null;
    }

    public void sendStaffMessage(String s) {
        JsonObject json = new JsonObject();
        json.addProperty("message", s);
        plugin.getNetworkHelper().getRedisPublisher().publishMessage("core_staff", json);
    }

    public void shutdown() {
        coreServer.setOnline(0);
        coreServer.setSlots(0);
        coreServer.setCurrentlyOnline(false);
        coreServer.setLastUpdate(new Date().getTime());

        JsonObject json = new JsonObject();
        json.addProperty("name", coreServer.getName());
        json.addProperty("type", coreServer.getType());
        json.addProperty("online", coreServer.getOnline());
        json.addProperty("slots", coreServer.getSlots());
        json.addProperty("last_update", coreServer.getLastUpdate());

        plugin.getNetworkHelper().getRedisPublisher().publishMessage("core_server_updates", json);
        this.sendStaffMessage("&c&l[Server Updater] &r&cServer &f" + coreServer.getName() + "&c has been shutdown.");
    }
}
