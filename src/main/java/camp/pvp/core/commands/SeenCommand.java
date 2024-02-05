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


public class SeenCommand implements CommandExecutor {

    private Core plugin;

    public SeenCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getCommand("seen").setExecutor(this);
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

        Player player = coreProfile.getPlayer();

        String seen = (player != null && player.isOnline() ? "online for &f" +
                DateUtils.getDifference(new Date(), coreProfile.getLastLogin()) : "offline for &f" + DateUtils.getDifference(new Date(), coreProfile.getLastLogout()));
        String name = coreProfile.getHighestRank().getColor() + coreProfile.getName();

        sender.sendMessage(Colors.get(name + " &6has been " + seen + "&6."));

        return true;
    }
}
