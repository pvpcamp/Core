package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.utils.PlayerUtils;
import camp.pvp.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DemoCommand {

    private Core plugin;
    public DemoCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Command(name = "demo",
            permission = "core.commands.demo",
            description = "Show the demo screen, and crash the client.")
    public void demo(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String[] args = commandArgs.getArgs();

        if(args.length > 0) {
            Player target = Bukkit.getPlayer(args[0]);
            if(target != null) {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + target.getName() + " now has 0 FPS.");
                PlayerUtils.showDemoScreen(target);
                target.setVelocity(target.getLocation().getDirection().multiply(5));

                Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                    PlayerUtils.crashClient(target);
                }, 5);
            } else {
                sender.sendMessage(ChatColor.RED + "The player you specified was not found on this server.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /demo <player>");
        }
    }
}
