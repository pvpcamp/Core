package camp.pvp.core.profiles;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.utils.Colors;
import camp.pvp.mongo.MongoCollectionResult;
import camp.pvp.mongo.MongoIterableResult;
import camp.pvp.mongo.MongoUpdate;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

@Getter @Setter
public class CoreProfileManager {

    private SpigotCore plugin;
    private Map<UUID, CoreProfile> loadedProfiles;
    private Map<UUID, PermissionAttachment> permissionAttachments;
    public CoreProfileManager(SpigotCore plugin) {
        this.plugin = plugin;
        this.loadedProfiles = new HashMap<>();
        this.permissionAttachments = new HashMap<>();

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
                player.removeAttachment(permissionAttachments.get(profile.getUuid()));
            }

            player.setOp(false);

            for (Map.Entry<String, Boolean> entry : profile.getPermissions(plugin.getCoreServer().getType()).entrySet()) {
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
            CoreProfile profile = getLoadedProfiles().get(player.getUniqueId());
            if(profile.isStaffMode()) {
                player.sendMessage(Colors.get("&7[Staff] " + message));
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

    public List<String> getProfilesWithRank(Rank rank) {
        List<String> profileNames = new ArrayList<>();
        plugin.getMongoManager().getCollection(false, "core_profiles", new MongoCollectionResult() {
            @Override
            public void call(MongoCollection<Document> mongoCollection) {
                MongoCursor<Document> cursor = mongoCollection.find(new Document("ranks", rank.getUuid())).cursor();
                while(cursor.hasNext()) {
                    profileNames.add(cursor.next().getString("name"));
                }
            }
        });

        return profileNames;
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

        if(!store && profile.getPlayer() == null) {
            getLoadedProfiles().remove(profile.getUuid());
        }
    }

    public void shutdown() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            CoreProfile profile = getLoadedProfiles().get(player.getUniqueId());
            exportToDatabase(profile, false, false);
        }
    }
}
