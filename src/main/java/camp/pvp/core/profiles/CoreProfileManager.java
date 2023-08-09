package camp.pvp.core.profiles;

import camp.pvp.core.Core;
import camp.pvp.core.listeners.redis.RedisProfileUpdateListener;
import camp.pvp.core.listeners.redis.StaffMessageListener;
import camp.pvp.core.profiles.tasks.NameMcVerifier;
import camp.pvp.core.utils.Colors;
import camp.pvp.mongo.MongoCollectionResult;
import camp.pvp.mongo.MongoManager;
import camp.pvp.mongo.MongoUpdate;
import camp.pvp.redis.RedisPublisher;
import camp.pvp.redis.RedisSubscriber;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

@Getter @Setter
public class CoreProfileManager {

    private Core plugin;
    private Map<UUID, CoreProfile> loadedProfiles;
    private Map<UUID, Grant> loadedGrants;
    private Map<UUID, ChatHistory> loadedHistory;
    private Map<UUID, PermissionAttachment> permissionAttachments;

    private MongoManager mongoManager;
    private String profilesCollection, chatHistoryCollection, grantsCollection;

    private RedisPublisher redisPublisher;
    private RedisSubscriber profileUpdateSubscriber, staffMessageSubscriber;
    public CoreProfileManager(Core plugin) {
        this.plugin = plugin;
        this.loadedProfiles = new HashMap<>();
        this.loadedGrants = new HashMap<>();
        this.loadedHistory = new HashMap<>();
        this.permissionAttachments = new HashMap<>();

        FileConfiguration config = plugin.getConfig();

        this.mongoManager = new MongoManager(plugin, config.getString("networking.mongo.uri"), config.getString("networking.mongo.database"));
        this.profilesCollection = config.getString("networking.mongo.profiles_collection");
        this.chatHistoryCollection = config.getString("networking.mongo.chat_history_collection");
        this.grantsCollection = config.getString("networking.mongo.grants_collection");

        this.redisPublisher = new RedisPublisher(plugin, config.getString("networking.redis.host"), config.getInt("networking.redis.port"));
        this.profileUpdateSubscriber = new RedisSubscriber(
                plugin,
                config.getString("networking.redis.host"),
                config.getInt("networking.redis.port"),
                "core_profile_updates",
                new RedisProfileUpdateListener(plugin));

        this.staffMessageSubscriber = new RedisSubscriber(
                plugin,
                config.getString("networking.redis.host"),
                config.getInt("networking.redis.port"),
                "core_staff",
                new StaffMessageListener(plugin));

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new NameMcVerifier(this), 0, 1200);

        plugin.getLogger().info("Started CoreProfileManager.");
    }

    public void updateAllPermissions() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            CoreProfile profile = loadedProfiles.get(player.getUniqueId());
            if(profile != null) {
                updatePermissions(profile);
            }
        }
    }

    public void updatePermissions(CoreProfile profile) {
        Player player = profile.getPlayer();
        if(player != null) {
            PermissionAttachment attachment = player.addAttachment(plugin);

            if (permissionAttachments.get(profile.getUuid()) != null) {
                if(permissionAttachments.get(profile.getUuid()) != null) {
                    player.removeAttachment(permissionAttachments.get(profile.getUuid()));
                }
            }

            player.setOp(false);

            for (Map.Entry<String, Boolean> entry : profile.getPermissions(plugin.getCoreServerManager().getCoreServer().getType()).entrySet()) {
                String permission = entry.getKey();
                if (permission.equalsIgnoreCase("*")) {
                    player.setOp(true);
                } else {
                    attachment.setPermission(permission, entry.getValue());
                }
            }

            permissionAttachments.put(profile.getUuid(), attachment);
        }
    }

    public void staffBroadcast(String message) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission("core.staff")) {
                player.sendMessage(Colors.get("&c[SB] " + message));
            }
        }
    }

    public CoreProfile find(UUID uuid, boolean store) {
        CoreProfile profile = loadedProfiles.get(uuid);
        if(profile == null) {
            profile = importFromDatabase(uuid, store);
            if(store) {
                loadedProfiles.put(uuid, profile);
            }
        }

        return profile;
    }

    public CoreProfile find(String name, boolean store) {
        if(!name.matches("^[a-zA-Z0-9_]{1,16}$")) {
            return null;
        }

        final CoreProfile[] profile = {null};

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getName().equalsIgnoreCase(name)) {
                return getLoadedProfiles().get(player.getUniqueId());
            }
        }

        getMongoManager().getCollection(false, profilesCollection, new MongoCollectionResult() {
            @Override
            public void call(MongoCollection<Document> mongoCollection) {
                mongoCollection.find(Filters.regex("name", "(?i)" + name)).forEach(
                    document -> {
                        String dbName = document.getString("name");
                        if(dbName.equalsIgnoreCase(name)) {
                            profile[0] = importFromDatabase(document.get("_id", UUID.class), store);
                        }
                    }
                );
            }
        });

        return profile[0];
    }

    public CoreProfile importFromDatabase(UUID uuid, boolean store) {
        final CoreProfile[] profile = {null};
        getMongoManager().getDocument(false, profilesCollection, uuid, document -> {
            if(document != null) {
                profile[0] = new CoreProfile(uuid);
                profile[0].importFromDocument(plugin, document);
                if(store) {
                    loadedProfiles.put(uuid, profile[0]);
                }
            }
        });

        return profile[0];
    }

    public CoreProfile create(Player player) {
        CoreProfile profile = new CoreProfile(player.getUniqueId());
        profile.setName(player.getName());
        profile.getRanks().add(plugin.getRankManager().getDefaultRank());
        profile.setFirstLogin(new Date());

        MongoUpdate mu = new MongoUpdate(profilesCollection, profile.getUuid());
        mu.setUpdate(profile.exportToMap());

        getMongoManager().massUpdate(false, mu);
        this.loadedProfiles.put(player.getUniqueId(), profile);
        return profile;
    }

    public void exportToDatabase(CoreProfile profile, boolean async, boolean store) {
        MongoUpdate mu = new MongoUpdate(profilesCollection, profile.getUuid());
        mu.setUpdate(profile.exportToMap());
        getMongoManager().massUpdate(async, mu);

        sendRedisUpdate(profile.getUuid());

        if(!store && profile.getPlayer() == null) {
            getLoadedProfiles().remove(profile.getUuid());
        }
    }

    public void sendRedisUpdate(UUID uuid) {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", uuid.toString());

        getRedisPublisher().publishMessage("core_profile_updates", json);
    }

    public Grant importGrant(UUID uuid, boolean async) {
        if(getLoadedGrants().containsKey(uuid)) {
            return getLoadedGrants().get(uuid);
        }

        final Grant[] grant = {null};
        getMongoManager().getDocument(async, grantsCollection, uuid, document -> {
            if(document != null) {
                grant[0] = new Grant(uuid);
                grant[0].importFromDocument(plugin, document);
            }
        });

        return grant[0];
    }

    public void exportGrant(Grant grant, boolean async) {
        MongoUpdate mu = new MongoUpdate(grantsCollection, grant.getUuid());
        mu.setUpdate(grant.exportToMap());
        getMongoManager().massUpdate(async, mu);
    }

    public ChatHistory importHistory(UUID uuid, boolean async) {
        final ChatHistory[] chatHistory = {null};
        getMongoManager().getDocument(async, chatHistoryCollection, uuid, document -> {
            if(document != null) {
                chatHistory[0] = new ChatHistory(document);
                this.getLoadedHistory().put(uuid, chatHistory[0]);
            }
        });

        return chatHistory[0];
    }

    public void exportHistory(ChatHistory history, boolean async) {
        MongoUpdate mu = new MongoUpdate(chatHistoryCollection, history.getUuid());
        mu.setUpdate(history.exportToMap());
        getMongoManager().massUpdate(async, mu);
    }

    public void shutdown() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            CoreProfile profile = getLoadedProfiles().get(player.getUniqueId());
            exportToDatabase(profile, false, false);
        }
    }
}
