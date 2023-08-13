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

            if(profile.getRanks().isEmpty()) {
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

            if(player.isOnline()) {

                cpm.updatePermissions(profile);

                if(player.hasPermission("core.staff") && profile.getAuthKey() == null) {
                    player.sendMessage(ChatColor.RED + "REMINDER: " + ChatColor.WHITE + "You need to set up two factor authentication on your account, please type /2fa setup.");
                }

                if(player.hasPermission("core.staff")) {
                    plugin.getCoreServerManager().sendStaffJoinMessage(player.getUniqueId(), profile.getHighestRank().getColor() + profile.getName());
                    if(profile.isStaffChat()) {
                        player.sendMessage(ChatColor.GREEN + "REMINDER: You are currently in staff chat.");
                    }
                } else {
                    profile.setStaffChat(false);
                }

                profile.setLoaded(true);
            }
        }
    }
}
