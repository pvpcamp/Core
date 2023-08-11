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
        if (args.length() == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }
        Player target = Bukkit.getPlayer(args.getArgs(0));
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
            player.sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <x> <y> <z> <direction> [player]");
            return;
        }
        try {
            switch (args.length()) {
                case 3: {
                    String x = args.getArgs(0);
                    String y = args.getArgs(1);
                    String z = args.getArgs(2);
                    teleport(player, player.getWorld(), x, y, z, "south");
                    player.sendMessage(Colors.get("&6You teleported to &f" + x + "&6, &f" + y + "&6, &f" + z + "&6."));
                    break;
                }
                case 4: {
                    String x = args.getArgs(0);
                    String y = args.getArgs(1);
                    String z = args.getArgs(2);
                    String d = args.getArgs(3);
                    teleport(player, player.getWorld(), x, y, z, d);
                    player.sendMessage(Colors.get("&6You teleported to &f" + x + "&6, &f" + y + "&6, &f" + z + "&6."));
                    break;
                }
                case 5: {
                    String x = args.getArgs(0);
                    String y = args.getArgs(1);
                    String z = args.getArgs(2);
                    String d = args.getArgs(3);
                    Player target = Bukkit.getPlayer(args.getArgs(4));
                    if (target == null) {
                        player.sendMessage(ChatColor.RED + args.getArgs(4) + " is not online.");
                        return;
                    }
                    teleport(target, target.getWorld(), x, y, z, d);
                    player.sendMessage(Colors.get("&6You teleported &f" + target.getName() + " &6to &f" + x + "&6, &f" + y + "&6, &f" + z + "&6."));
                    target.sendMessage(Colors.get("&6You were teleported to &f" + x + "&6, &f" + y + "&6, &f" + z + " &6by &f" + player.getName() + "&6."));
                    break;
                }
            }
        } catch (NumberFormatException ex) {
            player.sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <x> <y> <z> <direction> [player]");
        }
    }

    public void teleport(Player player, World world, String x, String y, String z, String direction) {
        double xx;
        double yy;
        double zz;
        float d = 0;

        if (x.equalsIgnoreCase("~")) {
            xx = Double.parseDouble(String.valueOf(player.getLocation().getBlockX()));
            //xx = player.getLocation().getX();
        } else {
            xx = Double.parseDouble(x);
        }
        if (y.equalsIgnoreCase("~")) {
            yy = Double.parseDouble(String.valueOf(player.getLocation().getBlockY()));
            //yy = player.getLocation().getY();
        } else {
            yy = Double.parseDouble(y);
        }
        if (z.equalsIgnoreCase("~")) {
            zz = Double.parseDouble(String.valueOf(player.getLocation().getBlockZ()));
            //zz = player.getLocation().getZ();
        } else {
            zz = Double.parseDouble(z);
        }
        if (direction.equalsIgnoreCase("north")) {
            d = 180;
        } else if (direction.equalsIgnoreCase("south")) {
            d = 0;
        } else if (direction.equalsIgnoreCase("east")) {
            d = -90;
        } else if (direction.equalsIgnoreCase("west")) {
            d = 90;
        }
        Location teleportPosition = new Location(world, xx + 0.5, yy + 0.5, zz + 0.5, d, 0);
        player.teleport(teleportPosition);
    }
}

