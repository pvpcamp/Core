package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import camp.pvp.core.utils.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Date;


public class PlayerLookupCommand {

    private Core plugin;

    public PlayerLookupCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Command(name = "playerlookup", aliases = {"plookup", "lookup", "lu"}, description = "See detailed information about a player.", permission = "core.commands.playerlookup")
    public void playtime(CommandArgs args) {

        if (args.length() == 0) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }

        String target = args.getArgs(0);
        CoreProfile coreProfile = plugin.getCoreProfileManager().find(target, false);

        if (coreProfile == null) {
            args.getSender().sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
            return;
        }

        StringBuilder lookup = new StringBuilder();
        lookup.append("&6&lPlayer Lookup");
        lookup.append("\n&6Name: &f").append(coreProfile.getName());
        lookup.append("\n&6Rank: &f").append(coreProfile.getHighestRank().getColor()).append(coreProfile.getHighestRank().getDisplayName());
        lookup.append("\n&6Status: &f").append(Bukkit.getPlayer(target) != null && Bukkit.getPlayer(target).isOnline() ? "Online for " + DateUtils.getDifference(new Date(), coreProfile.getLastLogin()) : "Last seen " + DateUtils.getDifference(new Date(), coreProfile.getLastLogout()) + " ago");
        lookup.append("\n&6Playtime: &f").append(DateUtils.getTimeFormat(coreProfile.getCurrentPlaytime()));
        lookup.append("\n&6First Login: &f").append(coreProfile.getFirstLogin());
        lookup.append("\n&6Last Login: &f").append(coreProfile.getLastLogin());
        lookup.append("\n&6Last Logout: &f").append(coreProfile.getLastLogout());
        args.getSender().sendMessage(Colors.get(lookup.toString()));
    }
}
