package camp.pvp.core.punishments;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.listeners.redis.RedisProfileUpdateListener;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.mongo.MongoManager;
import camp.pvp.mongo.MongoUpdate;
import camp.pvp.redis.RedisPublisher;
import camp.pvp.redis.RedisSubscriber;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

@Getter @Setter
public class PunishmentManager {

    private SpigotCore plugin;
    private Map<UUID, Punishment> loadedPunishments;

    private MongoManager mongoManager;
    private String punishmentsCollection;
    public PunishmentManager(SpigotCore plugin) {
        this.plugin = plugin;
        this.loadedPunishments = new HashMap<>();

        FileConfiguration config = plugin.getConfig();

        this.mongoManager = new MongoManager(plugin, config.getString("networking.mongo.uri"), config.getString("networking.mongo.database"));
        this.punishmentsCollection = config.getString("networking.mongo.punishments_collection");

        plugin.getLogger().info("Started PunishmentManager.");
    }

    public List<Punishment> getPunishmentsIp(String ip) {
        List<Punishment> punishments = new ArrayList<>();
        getMongoManager().getCollection(false, punishmentsCollection,
                mongoCollection -> mongoCollection.find(Filters.regex("ip", "(?i)" + ip)).forEach(
                document -> {
                    UUID uuid = document.get("_id", UUID.class);
                    Punishment punishment = new Punishment(uuid);
                    punishment.importFromDocument(document);
                    getLoadedPunishments().put(uuid, punishment);
                    punishments.add(punishment);
                })
        );

        return punishments;
    }

    public Punishment importFromDatabase(UUID uuid) {
        final Punishment[] punishment = {null};
        getMongoManager().getDocument(false, punishmentsCollection, uuid, document -> {
            if (document != null) {
                punishment[0] = new Punishment(uuid);
                punishment[0].importFromDocument(document);
                getLoadedPunishments().put(uuid, punishment[0]);
            }
        });

        return punishment[0];
    }

    public void exportToDatabase(Punishment punishment, boolean async) {
        MongoUpdate mu = new MongoUpdate(punishmentsCollection, punishment.getUuid());
        mu.setUpdate(punishment.exportToMap());
        getMongoManager().massUpdate(async, mu);
    }

    public void delete(Punishment punishment, boolean async) {
        getLoadedPunishments().remove(punishment.getUuid());

        CoreProfile profile = getPlugin().getCoreProfileManager().find(punishment.getIssuedTo(), false);
        profile.getPunishments().removeIf(p -> p.getUuid().equals(punishment.getUuid()));

        getPlugin().getCoreProfileManager().exportToDatabase(profile, true, false);
        getMongoManager().deleteDocument(async, punishmentsCollection, punishment.getUuid());
    }
}
