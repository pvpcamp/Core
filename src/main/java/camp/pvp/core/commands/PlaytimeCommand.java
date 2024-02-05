package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import camp.pvp.core.utils.DateUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class PlaytimeCommand implements CommandExecutor {

    private Core plugin;

    public PlaytimeCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getCommand("playtime").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }

        CoreProfile coreProfile = plugin.getCoreProfileManager().find(args[0], false);

        if (coreProfile == null) {
            sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
            return true;
        }

        sender.sendMessage(Colors.get(coreProfile.getHighestRank().getColor() + coreProfile.getName() + "&6's playtime is &f" + DateUtils.getTimeFormat(coreProfile.getCurrentPlaytime()) + "&6."));

        return true;
    }
}
