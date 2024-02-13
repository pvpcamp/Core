package camp.pvp.core.punishments;

import camp.pvp.core.Core;
import camp.pvp.core.listeners.redis.RedisProfileUpdateListener;
import camp.pvp.core.listeners.redis.RedisPunishmentUpdateListener;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.mongo.MongoManager;
import camp.pvp.mongo.MongoUpdate;
import camp.pvp.redis.RedisPublisher;
import camp.pvp.redis.RedisSubscriber;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter @Setter
public class PunishmentManager {

    private Core plugin;
    private Map<UUID, Punishment> loadedPunishments;

    private MongoManager mongoManager;
    private MongoCollection<Document> punishmentsCollection;

    private RedisPublisher redisPublisher;
    private RedisSubscriber punishmentUpdateSubscriber;
    public PunishmentManager(Core plugin) {
        this.plugin = plugin;
        this.loadedPunishments = new HashMap<>();

        FileConfiguration config = plugin.getConfig();

        this.mongoManager = new MongoManager(plugin, config.getString("networking.mongo.uri"), config.getString("networking.mongo.database"));
        this.punishmentsCollection = mongoManager.getDatabase().getCollection(config.getString("networking.mongo.punishments_collection"));

        this.redisPublisher = new RedisPublisher(plugin, config.getString("networking.redis.host"), config.getInt("networking.redis.port"));
        this.punishmentUpdateSubscriber = new RedisSubscriber(
                plugin,
                config.getString("networking.redis.host"),
                config.getInt("networking.redis.port"),
                "core_punishment_updates",
                new RedisPunishmentUpdateListener(plugin));

        punishmentsCollection.find().forEach(document -> {
            UUID uuid = document.get("_id", UUID.class);
            Punishment punishment = new Punishment(uuid);
            punishment.importFromDocument(document);
            getLoadedPunishments().put(uuid, punishment);
        });

        plugin.getLogger().info("Started PunishmentManager.");
    }

    public List<Punishment> getPunishmentsIp(String ip) {
        List<Punishment> punishments = new ArrayList<>();

        getLoadedPunishments().forEach((uuid, punishment) -> {
            punishment.getIps().forEach(punishmentIp -> {
                if (punishmentIp.equalsIgnoreCase(ip)) {
                    punishments.add(punishment);
                }
            });
        });

        return punishments;
    }

    public List<Punishment> getPunishmentsIps(List<String> ips) {
        List<Punishment> punishments = new ArrayList<>();

        getLoadedPunishments().forEach((uuid, punishment) -> {
            punishment.getIps().forEach(punishmentIp -> {
                if (ips.contains(punishmentIp)) {
                    punishments.add(punishment);
                }
            });
        });

        return punishments;
    }

    public List<Punishment> getPunishmentsForPlayer(UUID u) {
        List<Punishment> punishments = new ArrayList<>();

        getLoadedPunishments().forEach((uuid, punishment) -> {
            if (punishment.getIssuedTo().equals(u)) {
                punishments.add(punishment);
            }
        });

        return punishments;
    }

    public CompletableFuture<List<Punishment>> importForPlayerAsync(UUID uuid) {
        CompletableFuture<List<Punishment>> future = CompletableFuture.supplyAsync(()-> {
            List<Punishment> punishments = new ArrayList<>();

            punishmentsCollection.find().filter(Filters.eq("issued_to", uuid)).forEach(document -> {
                Punishment punishment = new Punishment(uuid);
                punishment.importFromDocument(document);
                punishments.add(punishment);
                getLoadedPunishments().put(uuid, punishment);
            });

            return punishments;
        });

        future.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        return future;
    }

    public CompletableFuture<Punishment> importOneAsync(UUID uuid) {
        CompletableFuture<Punishment> future = CompletableFuture.supplyAsync(() -> {
            Document doc = punishmentsCollection.find().filter(Filters.eq("_id", uuid)).first();
            if(doc != null) {
                Punishment punishment = new Punishment(uuid);
                punishment.importFromDocument(doc);
                getLoadedPunishments().put(uuid, punishment);
                return punishment;
            }

            return null;
        });

        future.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        return future;
    }

    public void exportToDatabase(Punishment punishment) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> {

            Document document = punishmentsCollection.find(new Document("_id", punishment.getUuid())).first();
            if(document == null) {
                punishmentsCollection.insertOne(new Document("_id", punishment.getUuid()));
            }

            punishment.exportToMap().forEach((key, value) -> punishmentsCollection.updateOne(Filters.eq("_id", punishment.getUuid()), Updates.set(key, value)));
            sendRedisUpdate(punishment.getUuid(), false);
        });

        getLoadedPunishments().put(punishment.getUuid(), punishment);
    }

    public void sendRedisUpdate(UUID uuid, boolean deleted) {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", uuid.toString());
        json.addProperty("from_server", plugin.getCoreServerManager().getCoreServer().getName());
        json.addProperty("deleted", deleted);

        getRedisPublisher().publishMessage("core_profile_updates", json);
    }

    public void delete(Punishment punishment) {
        getLoadedPunishments().remove(punishment.getUuid());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> punishmentsCollection.deleteOne(Filters.eq("_id", punishment.getUuid())));

        sendRedisUpdate(punishment.getUuid(), true);
    }
}
