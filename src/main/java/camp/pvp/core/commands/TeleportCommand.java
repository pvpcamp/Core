package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TeleportCommand {

    @Command(name = "teleport", aliases = {"tp"}, permission = "core.commands.teleport", inGameOnly = true)
    public void teleport(CommandArgs args) {
        Player player = args.getPlayer();
        Player target = Bukkit.getPlayer(args.getArgs(0));
        if (args.length() == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        if (args.length() == 1) {
            if (target == null) {
                player.sendMessage(ChatColor.RED + args.getArgs(0) + " is not online.");
                return;
            }
            player.teleport(target);
            player.sendMessage(Colors.get("&6You teleported to &f" + target.getName() + "&6."));
        } else if (args.length() == 2) {
            Player target2 = Bukkit.getPlayer(args.getArgs(1));
            if (target == null) {
                player.sendMessage(ChatColor.RED + args.getArgs(0) + " is not online.");
                return;
            }
            if (target2 == null) {
                args.getPlayer().sendMessage(ChatColor.RED + args.getArgs(1) + " is not online.");
                return;
            }
            target.teleport(target2);
            player.sendMessage(Colors.get("&6You teleported &f" + target.getName() + " &6to &f" + target2.getName() + "&6."));
            target.sendMessage(Colors.get("&6You were teleported to &f" + target2.getName() + " &6by &f" + player.getName() + "&6."));
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player> [player]");
        }
    }

    @Command(name = "teleporthere", aliases = {"tphere", "s"}, permission = "core.commands.teleport", inGameOnly = true)
    public void teleportHere(CommandArgs args) {
        Player player = args.getPlayer();
        if (args.length() == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        if (args.length() == 1) {
            Player target = Bukkit.getPlayer(args.getArgs(0));
            if (target == null) {
                player.sendMessage(ChatColor.RED + args.getArgs(0) + " is not online.");
                return;
            }
            target.teleport(player);
            player.sendMessage(Colors.get("&6You teleported &f" + target.getName() + " &6to you."));
            target.sendMessage(Colors.get("&6You were teleported to &f" + player.getName() + " &6by &f" + player.getName() + "&6."));
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
        }
    }

    @Command(name = "teleportposition", aliases = {"teleportpos", "teleportp", "tppos", "tpposition"}, permission = "core.commands.teleport", inGameOnly = true)
    public void teleportPosition(CommandArgs args) {
        Player player = args.getPlayer();
        if (args.length() == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <x> <y> <z> <player>");
            return;
        }
        try {
            switch (args.length()) {
                case 3: {
                    String x = args.getArgs(0);
                    String y = args.getArgs(1);
                    String z = args.getArgs(2);
                    teleport(player, player.getWorld(), x, y, z);
                    player.sendMessage(Colors.get("&6You teleported to &f" + x + "&6, &f" + y + "&6, &f" + z + "&6."));
                    break;
                }
                case 4: {
                    Player target = Bukkit.getPlayer(args.getArgs(0));
                    if (target == null) {
                        player.sendMessage(ChatColor.RED + args.getArgs(0) + " is not online.");
                        return;
                    }
                    String x = args.getArgs(1);
                    String y = args.getArgs(2);
                    String z = args.getArgs(3);
                    teleport(target, target.getWorld(), x, y, z);
                    player.sendMessage(Colors.get("&6You teleported &f" + target.getName() + " &6to &f" + x + "&6, &f" + y + "&6, &f" + z + "&6."));
                    target.sendMessage(Colors.get("&6You were teleported to &f" + x + "&6, &f" + y + "&6, &f" + z + " &6by &f" + player.getName() + "&6."));
                    break;
                }
            }
        } catch (NumberFormatException ex) {
            player.sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " [player] <x> <y> <z>");
        }
    }

    public void teleport(Player player, World world, String x, String y, String z) {
        if (x.equals("~")) {
            x = String.valueOf(player.getLocation().getBlockX());
        } else if (y.equals("~")) {
            y = String.valueOf(player.getLocation().getBlockY());
        } else if (z.equals("~")) {
            z = String.valueOf(player.getLocation().getBlockZ());
        }
        Location teleportPosition = new Location(world, Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z));
        player.teleport(teleportPosition);
    }
}

