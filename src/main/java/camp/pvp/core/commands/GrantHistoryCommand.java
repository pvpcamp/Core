package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.guis.ranks.GrantHistoryGui;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.profiles.Grant;
import camp.pvp.core.utils.Colors;
import camp.pvp.mongo.MongoCollectionResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GrantHistoryCommand implements CommandExecutor {

    private Core plugin;
    public GrantHistoryCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("granthistory").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)) return true;

        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }

        CompletableFuture<CoreProfile> profileFuture = plugin.getCoreProfileManager().findAsync(args[0]);
        profileFuture.thenAcceptAsync(profile -> {
            if(profile == null) {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                return;
            }

            List<Grant> grants = new ArrayList<>();
            plugin.getCoreProfileManager()
                    .getMongoManager()
                    .getDatabase()
                    .getCollection(plugin.getCoreProfileManager().getGrantsCollectionName())
                    .find().filter(new Document("issued_to", profile.getUuid())).forEach(document -> {
                Grant grant = new Grant(document.get("_id", UUID.class));
                grant.importFromDocument(plugin, document);
                grants.add(grant);
            });

            new GrantHistoryGui(profile, grants).open(player);
        });

        return true;
    }
}
