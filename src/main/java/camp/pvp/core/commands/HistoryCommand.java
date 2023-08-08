package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.guis.punishments.HistoryGui;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HistoryCommand implements CommandExecutor {

    private Core plugin;
    public HistoryCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("history").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length > 0) {
                String target = args[0];

                CoreProfile targetProfile = plugin.getCoreProfileManager().find(target, false);

                if(targetProfile != null) {
                    if(targetProfile.getPunishments().size() > 0) {
                        new HistoryGui(targetProfile.getName() + " History", targetProfile.getPunishments(), false).open(player);
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Player " + ChatColor.WHITE +  targetProfile.getName() + ChatColor.GREEN + " does not have any punishments.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /history <player>");
            }
        }

        return true;
    }
}
