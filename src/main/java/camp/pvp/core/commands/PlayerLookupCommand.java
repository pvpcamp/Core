package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import camp.pvp.core.utils.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;


public class PlayerLookupCommand implements CommandExecutor {

    private Core plugin;

    public PlayerLookupCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getCommand("playerlookup").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }

        CoreProfile coreProfile = plugin.getCoreProfileManager().find(args[0], false);

        if (coreProfile == null) {
            sender.sendMessage(ChatColor.RED + "The target you specified does not have a profile on the network.");
            return true;
        }

        Player player = coreProfile.getPlayer();

        StringBuilder sb = new StringBuilder();
        sb.append("&6&lPlayer Lookup");
        sb.append("\n&6Name: &f").append(coreProfile.getName());
        sb.append("\n&6Rank: &f").append(coreProfile.getHighestRank().getColor()).append(coreProfile.getHighestRank().getDisplayName());
        sb.append("\n&6Status: &f").append(player != null && player.isOnline() ? "Online for " + DateUtils.getDifference(new Date(), coreProfile.getLastLogin()) : "Last seen " + DateUtils.getDifference(new Date(), coreProfile.getLastLogout()) + " ago");
        sb.append("\n&6Playtime: &f").append(DateUtils.getTimeFormat(coreProfile.getCurrentPlaytime()));
        sb.append("\n&6First Login: &f").append(coreProfile.getFirstLogin());
        sb.append("\n&6Last Login: &f").append(coreProfile.getLastLogin());
        sb.append("\n&6Last Logout: &f").append(coreProfile.getLastLogout());
        sender.sendMessage(Colors.get(sb.toString()));

        return true;
    }
}
