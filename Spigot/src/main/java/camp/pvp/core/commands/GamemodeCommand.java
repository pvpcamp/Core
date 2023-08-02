package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;


public class GamemodeCommand {

    @Command(name = "gamemode", aliases = {"gm"}, description = "Change a players gamemode.", permission = "core.commands.gamemode")
    public void gm(CommandArgs args) {
        args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <gamemode> <player>");
        return;
    }

    @Command(name = "gamemode.creative", aliases = {"gamemode.c", "gm.creative", "gm.c", "gm.1", "gmc", "gm1"}, permission = "core.commands.gamemode")
    public void gmc(CommandArgs args) {

        if (args.length() == 0) {
            if (args.getSender() instanceof Player) {
                args.getPlayer().setGameMode(GameMode.CREATIVE);
                args.getPlayer().sendMessage(Colors.get("&6Your gamemode has been updated to &fCREATIVE&6."));
            } else {
                args.getSender().sendMessage(ChatColor.RED + "Cannot execute this command as console.");
            }
            return;
        }

        String name = args.getArgs(0);

        if (Bukkit.getPlayer(name) != null) {
            Player target = Bukkit.getPlayer(name);
            target.setGameMode(GameMode.CREATIVE);
            target.sendMessage(Colors.get("&6Your gamemode has been updated to &fCREATIVE&6."));
            args.getSender().sendMessage(Colors.get("&6You have set &f" + target.getName() + "&6's gamemode to &fCREATIVE&6."));
        } else {
            args.getSender().sendMessage(ChatColor.RED + name + " is not online.");
        }
    }

    @Command(name = "gamemode.survival", aliases = {"gamemode.s", "gm.survival", "gm.s", "gm.0", "gms", "gm0"}, permission = "core.commands.gamemode")
    public void gms(CommandArgs args) {

        if (args.length() == 0) {
            if (args.getSender() instanceof Player) {
                args.getPlayer().setGameMode(GameMode.SURVIVAL);
                args.getPlayer().sendMessage(Colors.get("&6Your gamemode has been updated to &fSURVIVAL&6."));
            } else {
                args.getSender().sendMessage(ChatColor.RED + "Cannot execute this command as console.");
            }
            return;
        }

        String name = args.getArgs(0);

        if (Bukkit.getPlayer(name) != null) {
            Player target = Bukkit.getPlayer(name);
            target.setGameMode(GameMode.SURVIVAL);
            target.sendMessage(Colors.get("&6Your gamemode has been updated to &fSURVIVAL&6."));
            args.getSender().sendMessage(Colors.get("&6You have set &f" + target.getName() + "&6's gamemode to &fSURVIVAL&6."));
        } else {
            args.getSender().sendMessage(ChatColor.RED + name + " is not online.");
        }
    }

    @Command(name = "gamemode.adventure", aliases = {"gamemode.a", "gm.adventure", "gm.a", "gm.2", "gma", "gm2"}, permission = "core.commands.gamemode")
    public void gma(CommandArgs args) {

        if (args.length() == 0) {
            if (args.getSender() instanceof Player) {
                args.getPlayer().setGameMode(GameMode.ADVENTURE);
                args.getPlayer().sendMessage(Colors.get("&6Your gamemode has been updated to &fADVENTURE&6."));
            } else {
                args.getSender().sendMessage(ChatColor.RED + "Cannot execute this command as console.");
            }
            return;
        }

        String name = args.getArgs(0);

        if (Bukkit.getPlayer(name) != null) {
            Player target = Bukkit.getPlayer(name);
            target.setGameMode(GameMode.CREATIVE);
            target.sendMessage(Colors.get("&6Your gamemode has been updated to &fADVENTURE&6."));
            args.getSender().sendMessage(Colors.get("&6You have set &f" + target.getName() + "&6's gamemode to &fADVENTURE&6."));
        } else {
            args.getSender().sendMessage(ChatColor.RED + name + " is not online.");
        }
    }
}