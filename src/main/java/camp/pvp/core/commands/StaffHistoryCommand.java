package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.guis.punishments.HistoryGui;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.utils.Colors;
import camp.pvp.mongo.MongoCollectionResult;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class StaffHistoryCommand implements CommandExecutor {

    private Core plugin;
    public StaffHistoryCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("staffhistory").setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length > 0) {
                String target = args[0];

                CoreProfile profile = plugin.getCoreProfileManager().find(target, false);
                if(profile != null) {
                    player.sendMessage(Colors.get("&aLoading staff history of " + profile.getHighestRank().getColor() + profile.getName() + "&a."));
                    List<Punishment> punishments = new ArrayList<>();
                    plugin.getPunishmentManager().getMongoManager().getCollection(true, plugin.getConfig().getString("networking.mongo.punishments_collection"), new MongoCollectionResult() {
                                @Override
                                public void call(MongoCollection<Document> mongoCollection) {
                                    Date started = new Date();
                                    mongoCollection.find(new Document("issued_from", profile.getUuid())).forEach(
                                            document -> {
                                                UUID punishmentId = document.get("_id", UUID.class);
                                                Punishment punishment = new Punishment(punishmentId);
                                                punishment.importFromDocument(document);
                                                plugin.getPunishmentManager().getLoadedPunishments().put(punishmentId, punishment);
                                                punishments.add(punishment);
                                            });

                                    new HistoryGui(profile.getName() + " Staff History", punishments, true).open(player);

                                }
                            }
                    );
                } else {
                    player.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            }
        }

        return true;
    }
}
