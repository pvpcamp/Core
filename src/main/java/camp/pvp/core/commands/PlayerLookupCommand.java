package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import camp.pvp.core.utils.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class PlayerLookupCommand implements CommandExecutor {

    private Core plugin;

    public PlayerLookupCommand(Core plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("playerlookup");
        command.setExecutor(this);
        command.setTabCompleter(new PlayerTabCompleter());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }

        CoreProfile senderProfile = sender instanceof Player player ? plugin.getCoreProfileManager().getLoadedProfile(player.getUniqueId()) : null;

        CompletableFuture<CoreProfile> profileFuture = plugin.getCoreProfileManager().findAsync(args[0]);
        profileFuture.thenAccept(profile -> {
            if(profile == null) {
                sender.sendMessage(ChatColor.RED + "The target you specified does not have a profile on the network.");
                return;
            }

            Player player = profile.getPlayer();

            StringBuilder sb = new StringBuilder();
            sb.append("&6&lPlayer Lookup");
            sb.append("\n&6Name: &f").append(profile.getName());
            sb.append("\n&6Rank: &f").append(profile.getHighestRank().getColor()).append(profile.getHighestRank().getDisplayName());
            sb.append("\n&6Status: &f").append(player != null && player.isOnline() ? "Online for " + DateUtils.getDifference(new Date(), profile.getLastLogin()) : "Last seen " + DateUtils.getDifference(new Date(), profile.getLastLogout()) + " ago");
            sb.append("\n&6Playtime: &f").append(DateUtils.getTimeFormat(profile.getCurrentPlaytime()));

            if (senderProfile != null) {
                sb.append("\n&6First Login: &f").append(senderProfile.convertToLocalTimeZone(profile.getFirstLogin()));
                sb.append("\n&6Last Login: &f").append(senderProfile.convertToLocalTimeZone(profile.getLastLogin()));
                sb.append("\n&6Last Logout: &f").append(senderProfile.convertToLocalTimeZone(profile.getLastLogout()));
            } else {
                sb.append("\n&6First Login: &f").append(profile.getFirstLogin());
                sb.append("\n&6Last Login: &f").append(profile.getLastLogin());
                sb.append("\n&6Last Logout: &f").append(profile.getLastLogout());
            }

            sb.append("\n&6Last Connected To Server: &f").append(profile.getLastConnectedServer());
            sender.sendMessage(Colors.get(sb.toString()));
        });

        return true;
    }
}
