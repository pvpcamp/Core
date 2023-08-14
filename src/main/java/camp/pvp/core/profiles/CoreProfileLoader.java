package camp.pvp.core.profiles;

import camp.pvp.core.Core;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class CoreProfileLoader implements Runnable{

    private Core plugin;
    private CoreProfileManager cpm;
    private Queue<Player> players;
    public CoreProfileLoader(Core plugin, CoreProfileManager cpm) {
        this.plugin = plugin;
        this.cpm = cpm;
        this.players = new LinkedList<>();
    }

    public void addToQueue(Player player) {
        players.add(player);
    }

    @Override
    public void run() {
        while(!players.isEmpty()) {
            final Player player = players.poll();
            CoreProfile profile = cpm.find(player.getUniqueId(), true);

            if(profile == null) {
                profile = cpm.create(player);
            }

            cpm.updatePermissions(profile);
        }
    }
}
