package camp.pvp.core.commands;

import camp.pvp.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandExecutor {

    private Core plugin;
    public TeleportCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getCommand("teleport").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        switch(label.toLowerCase()) {
            case "tp":
            case "teleport":
                teleport(player, label, args);
                break;
            case "tppos":
            case "teleportposition":
            case "tpp":
                teleportPosition(player, label, args);
                break;
            case "tphere":
            case "teleporthere":
                teleportHere(player, label, args);
                break;
        }

        return true;
    }

    public void teleport(Player sender, String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage("Usage: /" + label + " <player> [player]");
            return;
        }

        Player fromPlayer = null, toPlayer = null;

        switch(args.length) {
            case 1:
                fromPlayer = sender;
                toPlayer = Bukkit.getPlayer(args[0]);
                break;
            case 2:
                fromPlayer = Bukkit.getPlayer(args[0]);
                toPlayer = Bukkit.getPlayer(args[1]);
                break;
        }

        if(toPlayer == null) {
            sender.sendMessage(ChatColor.RED + "The target(s) you specified are not online.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "You have teleported " + ChatColor.WHITE + fromPlayer.getName() + ChatColor.GREEN + " to " + ChatColor.WHITE + toPlayer.getName() + ChatColor.GREEN + ".");
        fromPlayer.teleport(toPlayer.getLocation());
        fromPlayer.sendMessage(ChatColor.GREEN + "You have been teleported to " + ChatColor.WHITE + toPlayer.getName() + ChatColor.GREEN + ".");
    }

    public void teleportPosition(Player sender, String label, String[] args) {
        if(args.length < 3) {
            sender.sendMessage("Usage: /" + label + " <x> <y> <z> [player]");
            return;
        }

        Player toPlayer;
        if(args.length == 4) {
            toPlayer = Bukkit.getPlayer(args[3]);
        } else {
            toPlayer = sender;
        }

        if(toPlayer == null) {
            sender.sendMessage(ChatColor.RED + "The target you specified is not online.");
            return;
        }

        double x, y, z;
        try {
            x = Double.parseDouble(args[0]);
            y = Double.parseDouble(args[1]);
            z = Double.parseDouble(args[2]);
        } catch(NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid coordinates.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "You have teleported " + ChatColor.WHITE + toPlayer.getName() + ChatColor.GREEN + " to " + ChatColor.WHITE + x + " " + y + " " + z + ChatColor.GREEN + ".");
        toPlayer.teleport(new Location(toPlayer.getWorld(), x, y, z));
    }

    public void teleportHere(Player sender, String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage("Usage: /" + label + " <player>");
            return;
        }

        Player fromPlayer = Bukkit.getPlayer(args[0]);

        if(fromPlayer == null) {
            sender.sendMessage(ChatColor.RED + "The target you specified is not online.");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "You have teleported " + ChatColor.WHITE + fromPlayer.getName() + ChatColor.GREEN + " to you.");
        fromPlayer.teleport(sender.getLocation());
        fromPlayer.sendMessage(ChatColor.GREEN + "You have been teleported to " + ChatColor.WHITE + sender.getName() + ChatColor.GREEN + ".");
    }
}

