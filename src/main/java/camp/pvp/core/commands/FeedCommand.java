package camp.pvp.core.commands;

import camp.pvp.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand implements CommandExecutor {

    private Core plugin;
    public FeedCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getCommand("feed").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if(!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length == 0) {
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.sendMessage(ChatColor.GOLD + "You have been fed.");
            return true;
        }

        if (Bukkit.getPlayer(args[0]) != null) {
            Player target = Bukkit.getPlayer(args[0]);
            target.setFoodLevel(20);
            target.setSaturation(20);
            target.sendMessage(ChatColor.GOLD + "You have been fed");
            player.sendMessage(ChatColor.GOLD + "You have fed " + ChatColor.WHITE + target.getName() + ChatColor.GOLD + ".");
        } else {
            player.sendMessage(ChatColor.RED + args[0] + " is not online.");
        }

        return true;
    }
}
