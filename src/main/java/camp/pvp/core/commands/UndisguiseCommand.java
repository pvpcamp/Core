package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.Core;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UndisguiseCommand {

    private Core plugin;

    public UndisguiseCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Command(name = "undisguise", aliases = {"udisguise", "undis", "ud"}, permission = "core.commands.disguise", inGameOnly = true)
    public void undisguise(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0 || args.length() > 1) {
            if (!plugin.getDisguiseManager().checkState(player)) {
                player.sendMessage(ChatColor.RED + "You cannot do this in your current state.");
                return;
            }
            if (plugin.getDisguiseManager().isDisguised(player)) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    plugin.getDisguiseManager().undisguise(player, false);
                });
                player.sendMessage(ChatColor.GREEN + "You have undisguised.");
            } else {
                player.sendMessage(ChatColor.RED + "You are not disguised.");
            }
            return;
        }

        if (args.length() == 1) {
            if (player.hasPermission("core.commands.admindisguise")) {
                Player target = Bukkit.getPlayer(args.getArgs(0));
                if (target == null) {
                    player.sendMessage(ChatColor.RED + args.getArgs(0) + " is not online.");
                    return;
                }
                if (!plugin.getDisguiseManager().checkState(target)) {
                    player.sendMessage(ChatColor.RED + target.getName() + " cannot do that in their current state.");
                    return;
                }

                Rank targetRank = plugin.getCoreProfileManager().getLoadedProfiles().get(target.getUniqueId()).getHighestRank();
                if (plugin.getDisguiseManager().isDisguised(target)) {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        plugin.getDisguiseManager().undisguise(target, false);
                    });
                    Rank senderRank = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId()).getHighestRank();
                    player.sendMessage(Colors.get(ChatColor.GREEN + "You have undisguised " + targetRank.getColor() + target.getName() + ChatColor.GREEN + "."));
                    target.sendMessage(Colors.get(ChatColor.GREEN + "You have been undisguised by " + senderRank.getColor() + player.getName() + ChatColor.GREEN + "."));
                } else {
                    player.sendMessage(Colors.get(targetRank.getColor() + target.getName() + " &cis not disguised."));
                }
            } else {
                player.performCommand("ud");
            }
        }
    }
}
