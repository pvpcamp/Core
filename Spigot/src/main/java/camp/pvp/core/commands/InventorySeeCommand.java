package camp.pvp.core.commands;

import camp.pvp.core.SpigotCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InventorySeeCommand implements CommandExecutor {

    private SpigotCore plugin;
    public InventorySeeCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("inventorysee").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length > 0) {
                Player target = Bukkit.getPlayer(args[0]);
                if(target != null) {
                    player.sendMessage(ChatColor.GREEN + "Opened inventory of " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + ".");
                    player.openInventory(target.getInventory());
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
