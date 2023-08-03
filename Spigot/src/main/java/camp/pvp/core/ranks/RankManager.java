package camp.pvp.core.ranks;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.listeners.redis.RedisProfileUpdateListener;
import camp.pvp.core.listeners.redis.RedisRankUpdateListener;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.mongo.MongoIterableResult;
import camp.pvp.mongo.MongoManager;
import camp.pvp.mongo.MongoUpdate;
import camp.pvp.redis.RedisPublisher;
import camp.pvp.redis.RedisSubscriber;
import com.google.gson.JsonObject;
import com.mongodb.client.FindIterable;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class RankManager {

    private SpigotCore plugin;
    private Map<UUID, Rank> ranks;

    private MongoManager mongoManager;
    private String ranksCollection;

    private RedisPublisher redisPublisher;
    private RedisSubscriber rankUpdateSubscriber;
    public RankManager(SpigotCore plugin) {
        this.plugin = plugin;
        this.ranks = new HashMap<>();

        plugin.getLogger().info("Importing ranks from MongoDB.");

        FileConfiguration config = plugin.getConfig();

        this.mongoManager = new MongoManager(plugin, config.getString("networking.mongo.uri"), config.getString("networking.mongo.database"));
        this.ranksCollection = config.getString("networking.mongo.ranks_collection");

        this.redisPublisher = new RedisPublisher(plugin, config.getString("networking.redis.host"), config.getInt("networking.redis.port"));
        this.rankUpdateSubscriber = new RedisSubscriber(
                plugin,
                config.getString("networking.redis.host"),
                config.getInt("networking.redis.port"),
                "core_rank_updates",
                new RedisRankUpdateListener(plugin, this));

        getMongoManager().getCollectionIterable(false, ranksCollection, new MongoIterableResult() {
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
        getMongoManager().getDocument(false, ranksCollection, uuid, document -> {
            if(document != null) {
                rank[0] = new Rank(uuid);
                rank[0].importFromDocument(plugin, document);
                getRanks().put(uuid, rank[0]);
            }
        });

        for(CoreProfile profile : plugin.getCoreProfileManager().getLoadedProfiles().values()) {
            Rank oldRank = null;
            for(Rank r : profile.getRanks()) {
                if(r.getUuid().equals(uuid)) {
                    oldRank = r;
                    break;
                }
            }

            if(oldRank != null) {
                profile.getRanks().remove(oldRank);
                profile.getRanks().add(rank[0]);
            }
        }

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

        getMongoManager().deleteDocument(true, ranksCollection, rank.getUuid());

        Bukkit.getScheduler().runTaskLater(getPlugin(), ()-> this.sendRedisUpdate(rank, true), 10);
    }

    public void exportToDatabase(Rank rank, boolean async) {
        MongoUpdate mu = new MongoUpdate(ranksCollection, rank.getUuid());
        mu.setUpdate(rank.exportToMap());
        getMongoManager().massUpdate(async, mu);

        if(async) {
            Bukkit.getScheduler().runTaskLater(getPlugin(), ()-> this.sendRedisUpdate(rank, false), 10);
        } else {
            sendRedisUpdate(rank, false);
        }

        if(plugin.getCoreProfileManager() != null) {
            plugin.getCoreProfileManager().updateAllPermissions();
        }
    }

    public void sendRedisUpdate(Rank rank, boolean deleted) {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", rank.getUuid().toString());

        String server = getPlugin().getCoreServerManager().getCoreServer().getName();
        json.addProperty("from_server", server);
        json.addProperty("deleted", deleted);

        getRedisPublisher().publishMessage("core_rank_updates", json);
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
