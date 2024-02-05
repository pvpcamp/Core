package camp.pvp.core.commands;

import camp.pvp.core.utils.PlayerUtils;
import camp.pvp.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DemoCommand implements CommandExecutor {

    private Core plugin;
    public DemoCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getCommand("demo").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /demo <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) {
            sender.sendMessage(ChatColor.RED + "The player you specified was not found on this server.");
            return true;
        }

        sender.sendMessage(ChatColor.LIGHT_PURPLE + target.getName() + " now has 0 FPS.");
        PlayerUtils.showDemoScreen(target);
        target.setVelocity(target.getLocation().getDirection().multiply(5));

        Bukkit.getScheduler().runTaskLater(plugin, ()-> {
            PlayerUtils.crashClient(target);
        }, 5);

        return true;
    }
}
