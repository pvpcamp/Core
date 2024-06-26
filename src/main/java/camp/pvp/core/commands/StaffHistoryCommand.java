package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.guis.punishments.HistoryGui;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.utils.Colors;
import camp.pvp.mongo.MongoCollectionResult;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StaffHistoryCommand implements CommandExecutor{

    private Core plugin;
    public StaffHistoryCommand(Core plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("staffhistory");
        command.setExecutor(this);
        command.setTabCompleter(new PlayerTabCompleter());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)) return true;

        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }

        CoreProfile opener = plugin.getCoreProfileManager().getLoadedProfile(player.getUniqueId());

        CompletableFuture<CoreProfile> profileFuture = plugin.getCoreProfileManager().findAsync(args[0]);
        profileFuture.thenAccept(profile -> {
            if (profile == null) {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                return;
            }

            List<Punishment> punishments = new ArrayList<>();
            plugin.getPunishmentManager().getLoadedPunishments().forEach((uuid, punishment) -> {
                if(punishment.getIssuedFrom().equals(profile.getUuid())) {
                    punishments.add(punishment);
                }
            });

            new HistoryGui(profile, punishments, true, opener).open(player);
        });

        return true;
    }
}
