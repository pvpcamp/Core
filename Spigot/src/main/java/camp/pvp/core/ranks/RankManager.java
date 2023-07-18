package camp.pvp.core.ranks;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.mongo.MongoIterableResult;
import camp.pvp.mongo.MongoUpdate;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class RankManager {

    private SpigotCore plugin;
    private Map<UUID, Rank> ranks;
    public RankManager(SpigotCore plugin) {
        this.plugin = plugin;
        this.ranks = new HashMap<>();

        plugin.getLogger().info("Importing ranks from MongoDB.");

        plugin.getMongoManager().getCollectionIterable(false, "core_ranks", new MongoIterableResult() {
            @Override
            public void call(FindIterable<Document> iterable) {
                iterable.forEach(
                        document -> {
                            UUID uuid = document.get("_id", UUID.class);
                            Rank rank = new Rank(uuid);
                            rank.importFromDocument(plugin, document);
                            ranks.put(uuid, rank);
                        }
                );
            }
        });

        if(ranks.isEmpty()) {
            plugin.getLogger().info("No ranks were found in the database, creating the default rank.");
            Rank rank = new Rank(UUID.randomUUID());
            rank.setDefaultRank(true);
            exportToDatabase(rank, true);
            getRanks().put(rank.getUuid(), rank);
        } else {
            plugin.getLogger().info("Imported " + ranks.size() + " ranks from the database.");
        }
    }

    public Rank getRankFromName(String name) {
        for(Rank rank : getRanks().values()) {
            if(rank.getName().equalsIgnoreCase(name)) {
                return rank;
            }
        }

        return null;
    }

    public Rank getRankFromWeight(int weight) {
        for(Rank rank : getRanks().values()) {
            if(rank.getWeight() == weight) {
                return rank;
            }
        }

        return null;
    }

    public Rank importFromDatabase(UUID uuid) {
        final Rank[] rank = {null};
        plugin.getMongoManager().getDocument(false, "core_ranks", uuid, document -> {
            if(document != null) {
                rank[0] = new Rank(uuid);
                rank[0].importFromDocument(plugin, document);
                getRanks().put(uuid, rank[0]);
            }
        });

        return rank[0];
    }

    public Rank create(String name) {
        Rank rank = new Rank(UUID.randomUUID());
        rank.setName(name.toLowerCase());
        rank.setDisplayName(name);
        getRanks().put(rank.getUuid(), rank);

        exportToDatabase(rank, false);
        return rank;
    }

    public void delete(Rank rank) {
        getRanks().remove(rank.getUuid());

        for(CoreProfile profile : plugin.getCoreProfileManager().getLoadedProfiles().values()) {
            profile.getRanks().remove(rank);
        }

        plugin.getCoreProfileManager().updateAllPermissions();

        plugin.getMongoManager().deleteDocument(true, "core_ranks", rank.getUuid());
    }

    public void exportToDatabase(Rank rank, boolean async) {
        MongoUpdate mu = new MongoUpdate("core_ranks", rank.getUuid());
        mu.setUpdate(rank.exportToMap());
        plugin.getMongoManager().massUpdate(async, mu);

        if(plugin.getCoreProfileManager() != null) {
            plugin.getCoreProfileManager().updateAllPermissions();
        }
    }

    public Rank getDefaultRank() {
        for(Rank rank : getRanks().values()) {
            if(rank.isDefaultRank()) {
                return rank;
            }
        }

        return null;
    }

}
