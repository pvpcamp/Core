package camp.pvp.core.profiles;

import camp.pvp.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

public class PermissionUpdater implements Runnable{

    private Core plugin;
    private CoreProfileManager cpm;
    private Queue<CoreProfile> profiles;
    public PermissionUpdater(Core plugin, CoreProfileManager cpm) {
        this.plugin = plugin;
        this.cpm = cpm;
        this.profiles = new LinkedList<>();
    }

    public void addToQueue(CoreProfile profile) {
        profiles.add(profile);
    }

    @Override
    public void run() {
        while(!profiles.isEmpty()) {
            CoreProfile profile = profiles.poll();
            Player player = profile.getPlayer();
            cpm.updatePermissions(profile);

            if(!profile.isLoaded() && player != null && player.isOnline()) {

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
