package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.Core;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CheckDisguiseCommand {

    private Core plugin;

    public CheckDisguiseCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Command(name= "checkdisguise", aliases = {"checkdis", "checkd", "cd", "cdisguise", "chkdis"}, permission = "core.staff")
    public void checkDisguise(CommandArgs args) {

        if (args.length() == 0) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args.getArgs(0));

        if (target == null) {
            args.getSender().sendMessage(ChatColor.RED + args.getArgs(0) + " is not online.");
            return;
        }

        Rank playerRank = plugin.getCoreProfileManager().getLoadedProfiles().get(target.getUniqueId()).getHighestRank();
        if (plugin.getDisguiseManager().isDisguised(target)) {
            Rank disguisedRank = plugin.getDisguiseManager().getRank(target);
            args.getSender().sendMessage(Colors.get(playerRank.getColor() + plugin.getCoreProfileManager().getLoadedProfiles().get(target.getUniqueId()).getName() + " &6is currently " + disguisedRank.getColor() + target.getName() + "&6."));
        } else {
            args.getSender().sendMessage(Colors.get(playerRank.getColor() + target.getName() + " &cis not disguised."));
        }
    }
}
