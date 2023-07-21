package camp.pvp.core.commands.essentials;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

public class ListCommand implements CommandExecutor {

    private SpigotCore plugin;
    public ListCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("list").setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        List<CoreProfile> profiles = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()) {
            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
            if(profile != null) {
                profiles.add(profile);
            }
        }

        Collections.sort(profiles);

        StringBuilder sb = new StringBuilder();

        sb.append("&6Online &7(" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getServer().getMaxPlayers() + "): &f");

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
