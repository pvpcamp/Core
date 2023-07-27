package camp.pvp.core.commands;

import camp.pvp.core.utils.PlayerUtils;
import camp.pvp.core.SpigotCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DemoCommand implements CommandExecutor {

    private SpigotCore plugin;
    public DemoCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("demo").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0) {
            Player target = Bukkit.getPlayer(args[0]);
            if(target != null) {
                PlayerUtils.showDemoScreen(target);
            } else {
                sender.sendMessage(ChatColor.RED + "The player you specified was not found on this server.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /demo <player>");
        }
        return true;
    }
}
