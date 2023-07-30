package camp.pvp.core.commands.impl;

import camp.pvp.core.SpigotCore;
import camp.pvp.mongo.MongoCollectionResult;
import camp.pvp.mongo.MongoIterableResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.UUID;

public class WipePunishmentsCommand implements CommandExecutor {

    private SpigotCore plugin;

    public WipePunishmentsCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getCommand("wipepunishments").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        String punishmentsCollection = plugin.getConfig().getString("networking.mongo.punishments_collection");
        String profilesCollection = plugin.getConfig().getString("networking.mongo.profiles_collection");

        plugin.getPunishmentManager().getMongoManager().getCollection(true, punishmentsCollection, new MongoCollectionResult() {
            @Override
            public void call(MongoCollection<Document> mongoCollection) {
                mongoCollection.drop();
            }
        });

        plugin.getCoreProfileManager().getMongoManager().getCollectionIterable(true, profilesCollection, new MongoIterableResult() {
            @Override
            public void call(FindIterable<Document> findIterable) {
                findIterable.forEach(document -> {
                    plugin.getCoreProfileManager().getMongoManager().getCollection(true, profilesCollection, mongoCollection -> {
                        mongoCollection.updateOne(Filters.eq("_id", document.get("_id")), Updates.set("punishments", new ArrayList<UUID>()));
                    });
                });
            }
        });

        plugin.getPunishmentManager().getLoadedPunishments().clear();
        plugin.getCoreProfileManager().getLoadedProfiles().forEach((uuid, coreProfile) -> {
            coreProfile.getPunishments().clear();
        });

        sender.sendMessage(ChatColor.GREEN + "You have wiped all punishments.");
        return true;
    }
}