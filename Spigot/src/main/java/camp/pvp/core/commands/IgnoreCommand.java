package camp.pvp.core.commands;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IgnoreCommand implements CommandExecutor {

    private SpigotCore plugin;
    public IgnoreCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("ignore").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
            if(args.length > 0) {
                Player target = Bukkit.getPlayer(args[0]);
                if(target != null && player != target) {
                    if(!profile.getIgnored().contains(target.getUniqueId())) {
                        profile.getIgnored().add(target.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "You will no longer receive any messages from " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + ".");
                    } else {
                        player.sendMessage(ChatColor.RED + "You are already ignoring " + target.getName());
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
