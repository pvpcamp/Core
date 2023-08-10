package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.Core;
import camp.pvp.core.disguise.UUIDFetcher;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;


public class AdminDisguiseCommand {

    private Core plugin;

    public AdminDisguiseCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Command(name = "admindisguise", aliases = {"admind", "adisguise", "ad"}, permission = "core.commands.admindisguise", inGameOnly = true)
    public void adminDisguise(CommandArgs args) {
        Player player = args.getPlayer();
        CoreProfile coreProfile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if (args.length() == 0 || args.length() > 3) {
            player.sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <disguise> [rank] [target]");
            return;
        }

        Rank rank = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId()).getHighestRank();
        String disguise = args.getArgs(0);

        if (!disguise.matches("^[a-zA-Z0-9_]{1,16}$") || disguise.length() > 16) {
            player.sendMessage(ChatColor.RED + "Invalid username.");
            return;
        }

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (plugin.getCoreProfileManager().getLoadedProfiles().get(all.getUniqueId()).getName().equals(disguise) || all.getName().equalsIgnoreCase(disguise)) {
                player.sendMessage(ChatColor.RED + "You cannot disguise as this player as they're online.");
                return;
            }
        }

        if (args.length() == 1) {
            if (!plugin.getDisguiseManager().checkState(player)) {
                player.sendMessage(ChatColor.RED + "You cannot do this in your current state.");
                return;
            }
            if (plugin.getDisguiseManager().isDisguised(player)) {
                player.sendMessage(ChatColor.RED + "You are already disguised.");
                return;
            }
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                plugin.getDisguiseManager().disguise(player, disguise, coreProfile.getHighestRank(), true, false);
            });
            player.sendMessage(Colors.get(ChatColor.GREEN + "You have disguised as " + rank.getColor() + disguise + ChatColor.GREEN + "."));
            return;
        }

        String disguiseRank = args.getArgs(1);
        Rank dRank = plugin.getRankManager().getRankFromName(disguiseRank.toLowerCase());
        if (dRank == null) {
            player.sendMessage(ChatColor.RED + disguiseRank + " rank doesn't exist.");
            return;
        }

        if (args.length() == 2) {
            if (!plugin.getDisguiseManager().checkState(player)) {
                player.sendMessage(ChatColor.RED + "You cannot do this in your current state.");
                return;
            }
            if (plugin.getDisguiseManager().isDisguised(player)) {
                player.sendMessage(ChatColor.RED + "You are already disguised.");
                return;
            }
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                plugin.getDisguiseManager().disguise(player, disguise, dRank, true, false);
            });
            player.sendMessage(Colors.get(ChatColor.GREEN + "You have disguised as " + dRank.getColor() + disguise + ChatColor.GREEN + "."));
            return;
        }

        if (args.length() == 3) {
            Player target = Bukkit.getPlayer(args.getArgs(2));
            if (target == null) {
                player.sendMessage(ChatColor.RED + args.getArgs(2) + " is not online.");
                return;
            }

            if (!plugin.getDisguiseManager().checkState(target)) {
                player.sendMessage(ChatColor.RED + target.getName() + " cannot do this in their current state.");
                return;
            }
            if (disguise.equalsIgnoreCase(target.getName())) {
                player.sendMessage(ChatColor.RED + "You cannot disguise " + target.getName() + " as themself");
                return;
            }

            Rank targetRank = plugin.getCoreProfileManager().getLoadedProfiles().get(target.getUniqueId()).getHighestRank();
            if (plugin.getDisguiseManager().isDisguised(target)) {
                player.sendMessage(ChatColor.RED + target.getName() + " is already disguised.");
            } else {
                String name = target.getName();
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    plugin.getDisguiseManager().disguise(target, disguise, dRank, true, false);
                });
                player.sendMessage(Colors.get(ChatColor.GREEN + "You have disguised " + targetRank.getColor() + name + ChatColor.GREEN + " as " + dRank.getColor() + disguise + ChatColor.GREEN + "."));
                target.sendMessage(Colors.get(ChatColor.GREEN + "You were disguised as " + dRank.getColor() + disguise + ChatColor.GREEN + " by " + rank.getColor() + player.getName() + ChatColor.GREEN + "."));
            }
        }
    }
}