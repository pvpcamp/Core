package camp.pvp.core.commands.impl;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import camp.pvp.core.utils.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Date;


public class PlaytimeCommand {

    @Command(name = "playtime", aliases = {"pt"}, description = "Check the playtime of a player.", permission = "core.commands.playtime")
    public void playtime(CommandArgs args) {

        if (args.length() == 0) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }

        String target = args.getArgs(0);
        CoreProfile coreProfile = SpigotCore.getInstance().getCoreProfileManager().find(target, false);

        if (coreProfile == null) {
            args.getSender().sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
            return;
        }

        args.getSender().sendMessage(Colors.get(coreProfile.getHighestRank().getColor() + coreProfile.getName() + "&6's playtime is &f" + DateUtils.getTimeFormat(coreProfile.getCurrentPlaytime()) + "&6."));
    }
}
