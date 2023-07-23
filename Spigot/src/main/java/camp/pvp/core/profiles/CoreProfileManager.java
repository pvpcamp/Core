package camp.pvp.core.profiles;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.listeners.redis.RedisProfileUpdateListener;
import camp.pvp.core.utils.Colors;
import camp.pvp.mongo.MongoCollectionResult;
import camp.pvp.mongo.MongoUpdate;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

@Getter @Setter
public class CoreProfileManager {

    private SpigotCore plugin;
    private Map<UUID, CoreProfile> loadedProfiles;
    private Map<UUID, Grant> loadedGrants;
    private Map<UUID, ChatHistory> loadedHistory;
    private Map<UUID, PermissionAttachment> permissionAttachments;
    public CoreProfileManager(SpigotCore plugin) {
        this.plugin = plugin;
        this.loadedProfiles = new HashMap<>();
        this.loadedGrants = new HashMap<>();
        this.loadedHistory = new HashMap<>();
        this.permissionAttachments = new HashMap<>();

        plugin.getLogger().info("Started CoreProfileManager.");

        new RedisProfileUpdateListener(plugin);
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
                player.removeAttachment(permissionAttachments.get(profile.getUuid()));
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
                player.sendMessage(Colors.get("&7[Staff Broadcast] " + message));
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
        final CoreProfile[] profile = {null};

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getName().equalsIgnoreCase(name)) {
                return getLoadedProfiles().get(player.getUniqueId());
            }
        }

        plugin.getMongoManager().getCollection(false, "core_profiles", new MongoCollectionResult() {
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
        plugin.getMongoManager().getDocument(false, "core_profiles", uuid, document -> {
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

        MongoUpdate mu = new MongoUpdate("core_profiles", profile.getUuid());
        mu.setUpdate(profile.exportToMap());

        plugin.getMongoManager().massUpdate(false, mu);
        this.loadedProfiles.put(player.getUniqueId(), profile);
        return profile;
    }

    public void exportToDatabase(CoreProfile profile, boolean async, boolean store) {
        MongoUpdate mu = new MongoUpdate("core_profiles", profile.getUuid());
        mu.setUpdate(profile.exportToMap());
        plugin.getMongoManager().massUpdate(async, mu);

        sendRedisUpdate(profile.getUuid());

        if(!store && profile.getPlayer() == null) {
            getLoadedProfiles().remove(profile.getUuid());
        }
    }

    public void sendRedisUpdate(UUID uuid) {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", uuid.toString());

        plugin.getNetworkHelper().getRedisPublisher().publishMessage("core_profile_updates", json);
    }

    public Grant importGrant(UUID uuid, boolean async) {
        if(getLoadedGrants().containsKey(uuid)) {
            return getLoadedGrants().get(uuid);
        }

        final Grant[] grant = {null};
        plugin.getMongoManager().getDocument(async, "core_grants", uuid, document -> {
            if(document != null) {
                grant[0] = new Grant(uuid);
                grant[0].importFromDocument(plugin, document);
            }
        });

        return grant[0];
    }

    public void exportGrant(Grant grant, boolean async) {
        MongoUpdate mu = new MongoUpdate("core_grants", grant.getUuid());
        mu.setUpdate(grant.exportToMap());
        plugin.getMongoManager().massUpdate(async, mu);
    }

    public ChatHistory importHistory(UUID uuid, boolean async) {
        final ChatHistory[] chatHistory = {null};
        plugin.getMongoManager().getDocument(async, "core_chat_history", uuid, document -> {
            if(document != null) {
                chatHistory[0] = new ChatHistory(document);
                this.getLoadedHistory().put(uuid, chatHistory[0]);
            }
        });

        return chatHistory[0];
    }

    public void exportHistory(ChatHistory history, boolean async) {
        MongoUpdate mu = new MongoUpdate("core_chat_history", history.getUuid());
        mu.setUpdate(history.exportToMap());
        plugin.getMongoManager().massUpdate(async, mu);
    }

    public void shutdown() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            CoreProfile profile = getLoadedProfiles().get(player.getUniqueId());
            exportToDatabase(profile, false, false);
        }
    }
}
