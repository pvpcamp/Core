package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnignoreCommand implements CommandExecutor {

    private Core plugin;
    public UnignoreCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("unignore").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
            if(args.length > 0) {
                Player target = Bukkit.getPlayer(args[0]);
                if(target != null) {
                    if(profile.getIgnored().contains(target.getUniqueId())) {
                        profile.getIgnored().remove(target.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "You can now receive messages from " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + " again.");
                    } else {
                        player.sendMessage(ChatColor.RED + "You are not ignoring " + target.getName());
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "The player you specified is not on this server.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            }
        }

        return true;
    }
}
