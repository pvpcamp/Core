package camp.pvp.core.commands;

import camp.pvp.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class GamemodeCommand implements CommandExecutor {

    private Core plugin;
    public GamemodeCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getCommand("gamemode").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) return true;

        GameMode gameMode = null;
        Player target = (Player) sender;
        int targetArg = 0;
        switch(label.toLowerCase()) {
            case "gmc":
            case "creative":
                gameMode = GameMode.CREATIVE;
                break;
            case "gms":
            case "survival":
                gameMode = GameMode.SURVIVAL;
                break;
            default:
                targetArg = 1;
        }

        if(gameMode == null && args.length == 0) {
            sender.sendMessage("Usage: /" + label + " <gamemode> [player]");
            return true;
        }

        if(gameMode == null) {
            try {
                int i = Integer.parseInt(args[0]);
                if(i < 0 || i > 3) {
                    sender.sendMessage(ChatColor.RED + "Invalid gamemode.");
                    return true;
                }

                gameMode = GameMode.getByValue(i);
            } catch (NumberFormatException ignored) {
            }

            if(gameMode == null) {
                try {
                    gameMode = GameMode.valueOf(args[0].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid gamemode.");
                    return true;
                }
            }
        }

        if(args.length > targetArg) {
            target = Bukkit.getPlayer(args[targetArg]);
        }

        if(target == null) {
            sender.sendMessage("The target you specified is not on this server.");
            return true;
        }

        target.setGameMode(gameMode);
        target.sendMessage(ChatColor.GREEN + "Your gamemode has been updated to " + gameMode.toString() + ".");

        if(target != sender) sender.sendMessage(ChatColor.GREEN + "You have updated " + target.getName() + "'s gamemode to " + gameMode.toString() + ".");

        return true;
    }
}