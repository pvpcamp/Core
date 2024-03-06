package camp.pvp.core.punishments;

import camp.pvp.core.Core;
import camp.pvp.core.listeners.redis.RedisProfileUpdateListener;
import camp.pvp.core.listeners.redis.RedisPunishmentUpdateListener;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.mongo.MongoManager;
import camp.pvp.mongo.MongoUpdate;
import camp.pvp.redis.RedisPublisher;
import camp.pvp.redis.RedisSubscriber;
import com.avaje.ebean.text.json.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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

        plugin.getLogger().info("Started PunishmentManager.");
    }

    public List<Punishment> getPunishmentsIps(List<String> ips) {
        List<Punishment> punishments = new ArrayList<>();

        getPunishmentsCollection().find(Filters.in("ips", ips)).forEach(document -> {

            Punishment punishment = getLoadedPunishments().get(document.get("_id", UUID.class));
            if(punishment != null) {
                Date date = document.getDate("last_update");
                if(date == punishment.getLastUpdate()) return;
            }

            punishment = new Punishment(document.get("_id", UUID.class));
            punishment.importFromDocument(document);
            punishments.add(punishment);
            getLoadedPunishments().put(punishment.getUuid(), punishment);
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

    public void importForPlayer(UUID uuid) {
        List<Punishment> punishments = new ArrayList<>();

        punishmentsCollection.find().filter(Filters.eq("issued_to", uuid)).forEach(document -> {

            Punishment punishment = getLoadedPunishments().get(document.get("_id", UUID.class));
            if(punishment != null) {
                Date date = document.get("last_update", new Date());
                if(date == punishment.getLastUpdate()) return;
            }

            punishment = new Punishment(document.get("_id", UUID.class));
            punishment.importFromDocument(document);
            punishments.add(punishment);
            getLoadedPunishments().put(uuid, punishment);
        });
    }

    public CompletableFuture<Punishment> importOneAsync(UUID uuid) {

        Punishment loadedPunishment = getLoadedPunishments().get(uuid);

        CompletableFuture<Punishment> future = CompletableFuture.supplyAsync(() -> {
            Document doc = punishmentsCollection.find().filter(Filters.eq("_id", uuid)).first();
            if(doc != null) {

                Date date = doc.getDate("last_update");
                if(date == loadedPunishment.getLastUpdate()) return loadedPunishment;

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

        punishment.setLastUpdate(new Date());

        getLoadedPunishments().put(punishment.getUuid(), punishment);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> {

            Document document = punishmentsCollection.find(new Document("_id", punishment.getUuid())).first();
            if(document == null) {
                punishmentsCollection.insertOne(new Document("_id", punishment.getUuid()));
            }

            punishment.exportToMap().forEach((key, value) -> punishmentsCollection.updateOne(Filters.eq("_id", punishment.getUuid()), Updates.set(key, value)));
            sendRedisUpdate(punishment, false);
        });
    }

    public void sendRedisUpdate(Punishment punishment, boolean deleted) {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", punishment.getUuid().toString());
        json.addProperty("issued_to", punishment.getIssuedTo().toString());

        JsonArray ips = new JsonArray();
        punishment.getIps().forEach(ip -> ips.add(new JsonPrimitive(ip)));

        json.add("ips", ips);
        json.addProperty("from_server", plugin.getCoreServerManager().getCoreServer().getName());
        json.addProperty("deleted", deleted);

        getRedisPublisher().publishMessage("core_profile_updates", json);
    }

    public void delete(Punishment punishment) {
        getLoadedPunishments().remove(punishment.getUuid());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> punishmentsCollection.deleteOne(Filters.eq("_id", punishment.getUuid())));

        sendRedisUpdate(punishment, true);
    }
}
