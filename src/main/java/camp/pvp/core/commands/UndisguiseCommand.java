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

        if (args.length() == 0) {
            if (plugin.getDisguiseManager().isDisguised(player)) {
                plugin.getDisguiseManager().undisguise(player);
                player.sendMessage(ChatColor.GREEN + "You have undisguised.");
            } else {
                player.sendMessage(ChatColor.RED + "You are not disguised.");
            }
        } else {
            Player target = Bukkit.getPlayer(args.getArgs(0));
            if (target == null) {
                player.sendMessage(ChatColor.RED + args.getArgs(0) + " is not online.");
                return;
            }

            if (plugin.getDisguiseManager().isDisguised(target)) {
                plugin.getDisguiseManager().undisguise(target);
                Rank targetRank = plugin.getCoreProfileManager().getLoadedProfiles().get(target.getUniqueId()).getHighestRank();
                Rank senderRank = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId()).getHighestRank();
                player.sendMessage(Colors.get(ChatColor.GREEN + "You have undisguised " + targetRank.getColor() + target.getName() + ChatColor.GREEN + "."));
                target.sendMessage(Colors.get(ChatColor.GREEN + "You have been undisguised by " + senderRank.getColor() + player.getName() + ChatColor.GREEN + "."));
            }
        }
    }
}
