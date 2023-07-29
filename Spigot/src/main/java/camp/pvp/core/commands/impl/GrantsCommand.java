package camp.pvp.core.commands.impl;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.guis.ranks.GrantGui;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantsCommand implements CommandExecutor {

    private SpigotCore plugin;
    public GrantsCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("grants").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length > 0) {
            if(sender instanceof Player) {
                String name = args[0];
                Player player = (Player) sender;
                CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
                CoreProfile target = plugin.getCoreProfileManager().find(name, false);
                if (target != null) {
                    new GrantGui(plugin, profile, target).open(player);
                } else {
                    sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                }
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
        }

        return true;
    }
}
