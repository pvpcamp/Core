package camp.pvp.core.punishments;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.mongo.MongoUpdate;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.*;

@Getter @Setter
public class PunishmentManager {

    private SpigotCore plugin;
    private Map<UUID, Punishment> loadedPunishments;
    public PunishmentManager(SpigotCore plugin) {
        this.plugin = plugin;
        this.loadedPunishments = new HashMap<>();

        plugin.getLogger().info("Started PunishmentManager.");
    }

    /**
     * Find the punishments of either a player that was issued the punishments, or the player that issued the punishments.
     * @param uuid Player UUID.
     * @param field Must either be "issued_to" or "issued_from".
     * @return
     */
    public List<Punishment> getPunishmentsPlayerUUID(UUID uuid, String field) {
        List<Punishment> punishments = new ArrayList<>();
        plugin.getMongoManager().getCollection(false, "core_punishments",
                mongoCollection -> mongoCollection.find(new Document(field, uuid)).forEach(
                        document -> {
                            UUID punishmentId = document.get("_id", UUID.class);
                            Punishment punishment = new Punishment(punishmentId);
                            punishment.importFromDocument(document);
                            getLoadedPunishments().put(punishmentId, punishment);
                            punishments.add(punishment);
                        })
        );

        return punishments;
    }

    public List<Punishment> getPunishmentsIp(String ip) {
        List<Punishment> punishments = new ArrayList<>();
        plugin.getMongoManager().getCollection(false, "core_punishments",
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
        plugin.getMongoManager().getDocument(false, "core_punishments", uuid, document -> {
            if (document != null) {
                punishment[0] = new Punishment(uuid);
                punishment[0].importFromDocument(document);
                getLoadedPunishments().put(uuid, punishment[0]);
            }
        });

        return punishment[0];
    }

    public void exportToDatabase(Punishment punishment, boolean async) {
        MongoUpdate mu = new MongoUpdate("core_punishments", punishment.getUuid());
        mu.setUpdate(punishment.exportToMap());
        plugin.getMongoManager().massUpdate(async, mu);
    }
}
