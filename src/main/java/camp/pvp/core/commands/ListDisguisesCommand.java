package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.Core;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListDisguisesCommand {

    private Core plugin;

    public ListDisguisesCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Command(name = "listdisguises", aliases = {"listdis", "listd", "ld", "ldisguise"}, permission = "core.staff")
    public void listDisguises(CommandArgs args) {

        List<Player> disguisedPlayers = new ArrayList<>();

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (plugin.getDisguiseManager().isDisguised(p)) {
                disguisedPlayers.add(p);
            }
        });

        if (disguisedPlayers.isEmpty()) {
            args.getSender().sendMessage(ChatColor.RED + "There are currently no online disguised players.");
            return;
        }

        StringBuilder list = new StringBuilder();
        list.append(Colors.get("&6&lCurrent Online Disguises: "));
        disguisedPlayers.forEach(p -> {
            Rank playerRank = plugin.getCoreProfileManager().getLoadedProfiles().get(p.getUniqueId()).getHighestRank();
            Rank disguisedRank = (plugin.getDisguiseManager().getRank(p) == null ? playerRank : plugin.getDisguiseManager().getRank(p));
            list.append(Colors.get("\n " + disguisedRank.getColor() + p.getName() + " &6is currently " + playerRank.getColor() + plugin.getDisguiseManager().getRealUsername(p)));
        });
        args.getPlayer().sendMessage(list.toString());
    }
}
