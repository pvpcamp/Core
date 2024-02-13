package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ListCommand implements CommandExecutor {

    private Core plugin;
    public ListCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("list").setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        List<Rank> ranks = new ArrayList<>(plugin.getRankManager().getRanks().values());
        Collections.sort(ranks);

        List<CoreProfile> profiles = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()) {
            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfile(player.getUniqueId());
            if(profile != null) {
                profiles.add(profile);
            }
        }

        Collections.sort(profiles);
        StringBuilder sb = new StringBuilder();

        for(int x = 0; x < ranks.size(); x++) {
            Rank r = ranks.get(x);
            sb.append(r.getColor() + r.getDisplayName());

            if(x + 1 == ranks.size()) {
                sb.append("&7.");
            } else {
                sb.append("&7, ");
            }
        }

        sb.append("\n&f(" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getServer().getMaxPlayers() + "): &f");

        for(int x = 0; x < profiles.size(); x++) {
            CoreProfile p = profiles.get(x);
            sb.append(p.getHighestRank().getColor() + p.getName());

            if(x + 1 == profiles.size()) {
                sb.append("&7.");
            } else {
                sb.append("&7, ");
            }
        }

        sender.sendMessage(Colors.get(sb.toString()));

        return true;
    }
}
