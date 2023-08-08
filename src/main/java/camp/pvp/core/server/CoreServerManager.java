package camp.pvp.core.server;

import camp.pvp.core.Core;
import camp.pvp.core.listeners.redis.CoreServerListener;
import camp.pvp.redis.RedisPublisher;
import camp.pvp.redis.RedisSubscriber;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class CoreServerManager {

    private Core plugin;
    private CoreServer coreServer;
    private List<CoreServer> coreServers;
    private BukkitTask coreServerUpdater, currentServerUpdater, announcer;

    private RedisPublisher redisPublisher;
    private RedisSubscriber serverUpdateSubscriber;

    public CoreServerManager(Core plugin) {
        this.plugin = plugin;
        this.coreServers = new ArrayList<>();

        this.coreServer = new CoreServer(plugin.getConfig().getString("server.name"), plugin.getConfig().getString("server.type"));

        FileConfiguration config = plugin.getConfig();

        this.redisPublisher = new RedisPublisher(plugin, config.getString("networking.redis.host"), config.getInt("networking.redis.port"));
        this.serverUpdateSubscriber = new RedisSubscriber(
                plugin,
                config.getString("networking.redis.host"),
                config.getInt("networking.redis.port"),
                "core_server_updates",
                new CoreServerListener(this));

        this.coreServerUpdater = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                for(CoreServer server : getCoreServers()) {
                    if(server.isCurrentlyOnline()) {
                        if (new Date().getTime() - server.getLastUpdate() > 5000) {
                            server.setCurrentlyOnline(false);
                            plugin.getCoreProfileManager().staffBroadcast("&cServer &f" + server.getName() + " &chas not sent an update for 5 seconds, assuming offline.");
                        }
                    }
                }
            }
        }, 0, 100);

        this.announcer = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new ChatAnnouncer(plugin), 2400, 2400);

        this.currentServerUpdater = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
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
                json.addProperty("muted_chat", coreServer.isMutedChat());
                json.addProperty("last_update", coreServer.getLastUpdate());

                getRedisPublisher().publishMessage("core_server_updates", json);
            }
        }, 0, 40);
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
        json.addProperty("type", StaffMessageType.MESSAGE.toString());
        json.addProperty("message", s);
        getRedisPublisher().publishMessage("core_staff", json);
    }

    public void sendStaffJoinMessage(UUID uuid, String name) {
        JsonObject json = new JsonObject();
        json.addProperty("type", StaffMessageType.JOIN.toString());
        json.addProperty("uuid", uuid.toString());
        json.addProperty("name", name);
        json.addProperty("server", getCoreServer().getName());
        getRedisPublisher().publishMessage("core_staff", json);
    }

    public void sendStaffLeaveMessage(UUID uuid, String name) {
        JsonObject json = new JsonObject();
        json.addProperty("type", StaffMessageType.LEAVE.toString());
        json.addProperty("uuid", uuid.toString());
        json.addProperty("name", name);
        json.addProperty("server", getCoreServer().getName());
        getRedisPublisher().publishMessage("core_staff", json);
    }

    public void shutdown() {

    }
}
