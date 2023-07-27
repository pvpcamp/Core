package camp.pvp.core.commands;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.events.MongoGuiEvent;
import camp.pvp.core.guis.ranks.GrantGui;
import camp.pvp.core.guis.ranks.GrantHistoryGui;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.profiles.Grant;
import camp.pvp.core.utils.Colors;
import camp.pvp.events.MongoMessageEvent;
import camp.pvp.mongo.MongoCollectionResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

import java.util.*;

public class GrantHistoryCommand implements CommandExecutor {

    private SpigotCore plugin;
    public GrantHistoryCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("granthistory").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length > 0) {
            if(sender instanceof Player) {
                String name = args[0];
                Player player = (Player) sender;

                CoreProfile target = plugin.getCoreProfileManager().find(name, false);
                if (target != null) {
                    player.sendMessage(Colors.get("&aFetching grant history of " + target.getHighestRank().getColor() + target.getName() + "&a."));

                    plugin.getCoreProfileManager().getMongoManager().getCollection(true, plugin.getConfig().getString("networking.mongo.grants_collection"), new MongoCollectionResult() {
                        @Override
                        public void call(MongoCollection<Document> mongoCollection) {
                            Date requestStarted = new Date();
                            List<Grant> grants = new ArrayList<>();
                            MongoCursor<Document> cursor = mongoCollection.find(new Document("issued_to", target.getUuid())).cursor();
                            while(cursor.hasNext()) {
                                Document doc = cursor.next();
                                Grant grant = new Grant(doc.get("_id", UUID.class));
                                grant.importFromDocument(plugin, doc);
                                grants.add(grant);
                            }

                            Bukkit.getServer().getPluginManager().callEvent(new MongoGuiEvent(new GrantHistoryGui(target, grants), player.getUniqueId(), requestStarted));
                        }
                    });
                } else {
                    sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                }
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
        }

        return true;
    }
}
