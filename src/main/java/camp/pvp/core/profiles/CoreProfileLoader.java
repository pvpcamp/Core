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

            if(profile.getRanks().size() == 0) {
                profile.getRanks().add(plugin.getRankManager().getDefaultRank());
            }

            String ip = player.getAddress().getAddress().getHostAddress();

            if(profile.getIp() != null && !profile.getIp().equals(ip)) {
                if(profile.getAuthKey() != null) {
                    profile.setAuthenticated(false);
                    player.sendMessage(Colors.get("&c&lYour IP address has changed, please authenticate yourself."));
                    plugin.getCoreProfileManager().exportToDatabase(profile, true, true);
                }
            }

            profile.setIp(ip);
            profile.setLastLogin(new Date());

            if(!profile.getIpList().contains(ip)) {
                profile.getIpList().add(ip);
            }

            cpm.getPermissionUpdater().addToQueue(profile);
        }
    }
}
