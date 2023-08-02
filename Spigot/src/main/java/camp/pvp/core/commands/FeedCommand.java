package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FeedCommand {

    @Command(name = "feed", aliases = {"eat"}, description = "Feed a player.", permission = "core.commands.feed", inGameOnly = true)
    public void feed(CommandArgs args) {

        Player player = args.getPlayer();

        if (args.length() == 0) {
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.sendMessage(ChatColor.GOLD + "You have been fed.");
            return;
        }

        String name = args.getArgs(0);

        if (Bukkit.getPlayer(name) != null) {
            Player target = Bukkit.getPlayer(name);
            target.setFoodLevel(20);
            target.setSaturation(20);
            target.sendMessage(ChatColor.GOLD + "You have been fed");
            player.sendMessage(ChatColor.GOLD + "You have fed " + ChatColor.WHITE + target.getName() + ChatColor.GOLD + ".");
        } else {
            player.sendMessage(ChatColor.RED + name + " is not online.");
        }
    }
}
