package camp.pvp.core.commands;

import camp.pvp.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {

    private Core plugin;
    public HealCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getCommand("heal").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) return true;

        Player target = (Player) sender;

        if(args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
        }

        if(target == null) {
            sender.sendMessage("The target you specified is not on this server.");
            return true;
        }

        target.setHealth(target.getMaxHealth());
        target.setFoodLevel(20);
        target.setSaturation(13);
        target.sendMessage(ChatColor.GREEN + "You have been healed.");
        sender.sendMessage(ChatColor.GREEN + "You have healed " + target.getName() + ".");

        return true;
    }
}
