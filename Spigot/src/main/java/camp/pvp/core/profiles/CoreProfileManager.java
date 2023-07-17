package camp.pvp.core.profiles;

import camp.pvp.core.SpigotCore;
import camp.pvp.mongo.MongoCollectionResult;
import camp.pvp.mongo.MongoUpdate;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class CoreProfileManager {

    private SpigotCore plugin;
    private Map<UUID, CoreProfile> loadedProfiles;
    public CoreProfileManager(SpigotCore plugin) {
        this.plugin = plugin;
        this.loadedProfiles = new HashMap<>();

        plugin.getLogger().info("Started CoreProfileManager.");
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
        plugin.getMongoManager().getCollection(false, "core_profiles", new MongoCollectionResult() {
            @Override
            public void call(MongoCollection<Document> mongoCollection) {
                Document doc = mongoCollection.find(Filters.regex("name", "(?i)" + name)).first();
                if(doc != null) {
                    profile[0] = importFromDatabase(doc.get("_id", UUID.class), store);
                }
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

        if(!store) {
            getLoadedProfiles().remove(profile.getUuid());
        }
    }

    public void shutdown() {

    }
}
