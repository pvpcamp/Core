package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HealCommand {

    @Command(name = "heal", description = "Heal a player.", permission = "core.commands.heal", inGameOnly = true)
    public void heal(CommandArgs args) {

        Player player = args.getPlayer();

        if (args.length() == 0) {
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.sendMessage(ChatColor.GOLD + "You have been healed.");
            return;
        }

        String name = args.getArgs(0);

        if (Bukkit.getPlayer(name) != null) {
            Player target = Bukkit.getPlayer(name);
            target.setHealth(20);
            target.setFoodLevel(20);
            target.setSaturation(20);
            target.sendMessage(ChatColor.GOLD + "You have been healed.");
            player.sendMessage(ChatColor.GOLD + "You have healed " + ChatColor.WHITE + target.getName() + ChatColor.GOLD + ".");
        } else {
            player.sendMessage(ChatColor.RED + name + " is not online.");
        }
    }
}
