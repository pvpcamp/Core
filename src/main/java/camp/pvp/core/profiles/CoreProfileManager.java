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
import com.mongodb.client.model.Updates;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter @Setter
public class CoreProfileManager {

    private Core plugin;
    private Map<UUID, CoreProfile> loadedProfiles;
    private Map<UUID, Grant> loadedGrants;
    private Map<UUID, ChatHistory> loadedHistory;
    private Map<UUID, PermissionAttachment> permissionAttachments;

    private MongoManager mongoManager;
    private MongoCollection<Document> profilesCollection, chatHistoryCollection, grantsCollection;

    private RedisPublisher redisPublisher;
    private RedisSubscriber profileUpdateSubscriber, staffMessageSubscriber;
    private BukkitTask nameMcVerifier, flightEffectUpdater;

    public CoreProfileManager(Core plugin) {
        this.plugin = plugin;
        this.loadedProfiles = new HashMap<>();
        this.loadedGrants = new HashMap<>();
        this.loadedHistory = new HashMap<>();
        this.permissionAttachments = new HashMap<>();

        FileConfiguration config = plugin.getConfig();

        this.mongoManager = new MongoManager(plugin, config.getString("networking.mongo.uri"), config.getString("networking.mongo.database"));
        this.profilesCollection = mongoManager.getDatabase().getCollection(config.getString("networking.mongo.profiles_collection"));
        this.chatHistoryCollection = mongoManager.getDatabase().getCollection(config.getString("networking.mongo.chat_history_collection"));
        this.grantsCollection = mongoManager.getDatabase().getCollection(config.getString("networking.mongo.grants_collection"));

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

        this.nameMcVerifier = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new NameMcVerifier(this), 0, 1200);
        this.flightEffectUpdater = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                CoreProfile profile = getLoadedProfile(player.getUniqueId());
                if(profile.getAppliedFlightEffect().equals(FlightEffect.NONE)) continue;

                profile.getAppliedFlightEffect().playEffect(player);
            }
        }, 0, 2);

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
        if(player != null && player.isOnline()) {
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

    public CompletableFuture<CoreProfile> findAsync(UUID uuid) {
        CoreProfile loadedProfile = getLoadedProfile(uuid);
        if(loadedProfile != null) return CompletableFuture.supplyAsync(() -> loadedProfile);

        CompletableFuture<CoreProfile> profileFuture = CompletableFuture.supplyAsync(() -> {
            Document doc = profilesCollection.find().filter(Filters.eq("_id", uuid)).first();
            if(doc != null) {
                CoreProfile profile = new CoreProfile(uuid);
                profile.importFromDocument(plugin, doc);
                profile.setLastLoadFromDatabase(System.currentTimeMillis());

                loadedProfiles.put(uuid, profile);
                return profile;
            }

            return null;
        });

        profileFuture.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        return profileFuture;
    }

    public CompletableFuture<CoreProfile> findAsync(String name) {
        if(!name.matches("^[a-zA-Z0-9_]{1,16}$")) {
            return CompletableFuture.supplyAsync(() -> null);
        }

        CoreProfile loadedProfile = getLoadedProfile(name);
        if(loadedProfile != null) return CompletableFuture.supplyAsync(() -> loadedProfile);

        CompletableFuture<CoreProfile> profileFuture = CompletableFuture.supplyAsync(() -> {
            Document doc = profilesCollection.find().filter(Filters.regex("name", "(?i)" + name)).first();
            if(doc != null) {
                CoreProfile profile = new CoreProfile(doc.get("_id", UUID.class));
                profile.importFromDocument(plugin, doc);
                profile.setLastLoadFromDatabase(System.currentTimeMillis());
                loadedProfiles.put(profile.getUuid(), profile);
                return profile;
            }

            return null;
        });

        profileFuture.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        return profileFuture;
    }

    public CoreProfile getLoadedProfile(UUID uuid) {
        CoreProfile profile = loadedProfiles.get(uuid);
        if(profile == null || !profile.isCurrent()) return null;

        return profile;
    }

    public CoreProfile getLoadedProfile(String name) {
        for(CoreProfile profile : loadedProfiles.values()) {
            if(profile.getName().equalsIgnoreCase(name)) {
                return profile.isCurrent() ? profile : null;
            }
        }

        return null;
    }

    public CoreProfile preLogin(UUID uuid, String name, String ip) {
        CoreProfile profile = getLoadedProfile(uuid);
        if(profile == null) {
            Document doc = profilesCollection.find().filter(Filters.eq("_id", uuid)).first();
            if(doc != null) {
                profile = new CoreProfile(uuid);
                profile.importFromDocument(plugin, doc);
            }
        }

        boolean exists = true;
        if(profile == null) {
            exists = false;
            profile = new CoreProfile(uuid);
            profile.setFirstLogin(new Date());
            profile.setLastLogin(new Date());
        }

        profile.setName(name);
        profile.addIp(ip);
        profile.setLastLogin(new Date());
        profile.setLastLoadFromDatabase(System.currentTimeMillis());

        if(!exists) {

            Document document = profilesCollection.find(new Document("_id", profile.getUuid())).first();
            if(document == null) {
                profilesCollection.insertOne(new Document("_id", profile.getUuid()));
            }

            exportToDatabase(profile, false);
        }

        loadedProfiles.put(uuid, profile);
        return profile;
    }

    public void exportToDatabase(CoreProfile profile, boolean async) {
        if(async) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                profile.exportToMap().forEach((key, value) -> {
                    profilesCollection.updateOne(Filters.eq("_id", profile.getUuid()), Updates.set(key, value));
                });
            });
        } else {
            profile.exportToMap().forEach((key, value) -> {
                profilesCollection.updateOne(Filters.eq("_id", profile.getUuid()), Updates.set(key, value));
            });
        }

        sendRedisUpdate(profile.getUuid());
    }

    public void sendRedisUpdate(UUID uuid) {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", uuid.toString());

        getRedisPublisher().publishMessage("core_profile_updates", json);
    }

    public void exportGrant(Grant grant) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            Document document = grantsCollection.find(new Document("_id", grant.getUuid())).first();
            if(document == null) {
                grantsCollection.insertOne(new Document("_id", grant.getUuid()));
            }

            grant.exportToMap().forEach((key, value)
                    -> grantsCollection.updateOne(Filters.eq("_id", grant.getUuid()), Updates.set(key, value)));
        });
    }

    public void exportHistory(ChatHistory history) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            Document document = chatHistoryCollection.find(new Document("_id", history.getUuid())).first();
            if(document == null) {
                chatHistoryCollection.insertOne(new Document("_id", history.getUuid()));
            }

            history.exportToMap().forEach((key, value)
                    -> chatHistoryCollection.updateOne(Filters.eq("_id", history.getUuid()), Updates.set(key, value)));
        });
    }

    public void shutdown() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            CoreProfile profile = getLoadedProfiles().get(player.getUniqueId());
            exportToDatabase(profile, false);
        }

        nameMcVerifier.cancel();
    }
}
