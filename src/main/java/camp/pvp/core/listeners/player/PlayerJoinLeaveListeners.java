package camp.pvp.core.listeners.player;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.utils.Colors;
import camp.pvp.core.utils.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class PlayerJoinLeaveListeners implements Listener {

    private Core plugin;

    public PlayerJoinLeaveListeners(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        CoreProfile profile = plugin.getCoreProfileManager().importFromDatabase(uuid, true);
        Punishment punishment = null;

        if(profile != null) {
            punishment = profile.getActivePunishment(Punishment.Type.BLACKLIST);
            if(punishment == null) {
                punishment = profile.getActivePunishment(Punishment.Type.BAN);
            }
        }

        if(punishment == null) {
            List<Punishment> punishments = plugin.getPunishmentManager().getPunishmentsIp(event.getAddress().getHostAddress());
            for(Punishment p : punishments) {
                if((p.getType().equals(Punishment.Type.BAN) || p.getType().equals(Punishment.Type.BLACKLIST)) && p.isActive() && p.isIpPunished()) {
                    punishment = p;
                    break;
                }
            }
        }

        if(punishment != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Colors.get(
                    "\n " +
                    punishment.getType().getMessage() +
                    "\n " +
                    "\n&cReason: " + punishment.getReason() +
                    "\n&cExpires: " + (punishment.getExpires() == null ? "Never" : DateUtils.getDifference(punishment.getExpires(), new Date())) +
                    "\n" + punishment.getType().getAppealMessage()
            ));

            plugin.getCoreProfileManager().getLoadedProfiles().remove(punishment.getIssuedTo());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(profile == null) {
            plugin.getCoreProfileManager().getCoreProfileLoader().addToQueue(player);
        } else {
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

                plugin.getCoreProfileManager().updatePermissions(profile);

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

        List<String> welcomeMessages = plugin.getConfig().getStringList("messages.welcome");
        for(String s : welcomeMessages) {
            player.sendMessage(Colors.get(s));
        }

        player.setPlayerListHeaderFooter(Colors.get(plugin.getConfig().getString("tablist.header")), Colors.get(plugin.getConfig().getString("tablist.footer")));

        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(player.hasPermission("core.staff")) {
            plugin.getCoreServerManager().sendStaffLeaveMessage(player.getUniqueId(), profile.getHighestRank().getColor() + profile.getName());
        }

        if(profile != null) {
            profile.setLastLogout(new Date());

            profile.setPlaytime(profile.getPlaytime() + (profile.getLastLogout().getTime() - profile.getLastLogin().getTime()));

            plugin.getCoreProfileManager().exportToDatabase(profile, true, false);
        }

        plugin.getCoreProfileManager().getPermissionAttachments().remove(player.getUniqueId());

        event.setQuitMessage(null);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(profile != null) {
            profile.setLastLogout(new Date());

            profile.setPlaytime(profile.getPlaytime() + (profile.getLastLogout().getTime() - profile.getLastLogin().getTime()));

            plugin.getCoreProfileManager().exportToDatabase(profile, true, false);
        }
    }
}
