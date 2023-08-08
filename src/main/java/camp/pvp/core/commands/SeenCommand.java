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


public class SeenCommand {

    private Core plugin;

    public SeenCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Command(name = "seen", aliases = {"lastseen"}, description = "Check a players status.", permission = "core.commands.seen")
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

        String seen = (Bukkit.getPlayer(target) != null && Bukkit.getPlayer(target).isOnline() ? "online for &f" + DateUtils.getDifference(new Date(), coreProfile.getLastLogin()) : "offline for &f" + DateUtils.getDifference(new Date(), coreProfile.getLastLogout()));
        String name = coreProfile.getHighestRank().getColor() + coreProfile.getName();

        args.getSender().sendMessage(Colors.get(name + " &6has been " + seen + "&6."));
    }
}
