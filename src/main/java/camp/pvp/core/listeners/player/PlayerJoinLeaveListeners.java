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
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = plugin.getCoreProfileManager().find(player.getUniqueId(), true);

        List<String> welcomeMessages = plugin.getConfig().getStringList("messages.welcome");
        for(String s : welcomeMessages) {
            player.sendMessage(Colors.get(s));
        }

        if(profile == null) {
            profile = plugin.getCoreProfileManager().create(player);
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

        plugin.getCoreProfileManager().updatePermissions(profile);

        if(player.hasPermission("core.staff") && profile.getAuthKey() == null) {
            player.sendMessage(ChatColor.RED + "REMINDER: " + ChatColor.WHITE + "You need to set up two factor authentication on your account, please type /2fa setup.");
        }

        profile.setIp(ip);
        profile.setLastLogin(new Date());

        if(!profile.getIpList().contains(ip)) {
            profile.getIpList().add(ip);
        }

        if(player.hasPermission("core.staff")) {
            plugin.getCoreServerManager().sendStaffJoinMessage(player.getUniqueId(), profile.getHighestRank().getColor() + profile.getName());
            if(profile.isStaffChat()) {
                player.sendMessage(ChatColor.GREEN + "REMINDER: You are currently in staff chat.");
            }
        } else {
            profile.setStaffChat(false);
        }

        if (plugin.getDisguiseManager().getDisguiseMap().containsValue(player.getName())) {
            for (Map.Entry<UUID, String> entrySet : plugin.getDisguiseManager().getDisguiseMap().entrySet()) {
                if (entrySet.getValue().equalsIgnoreCase(player.getName())) {
                    Player target = Bukkit.getPlayer(entrySet.getKey());
                    plugin.getDisguiseManager().undisguise(player, true);
                    target.kickPlayer(ChatColor.RED + "Someone with your disguise name has logged on!");
                }
            }
        }

        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = plugin.getCoreProfileManager().find(player.getUniqueId(), true);

        if(player.hasPermission("core.staff")) {
            plugin.getCoreServerManager().sendStaffLeaveMessage(player.getUniqueId(), profile.getHighestRank().getColor() + profile.getName());
        }

        if(profile != null) {
            profile.setLastLogout(new Date());

            profile.setPlaytime(profile.getPlaytime() + (profile.getLastLogout().getTime() - profile.getLastLogin().getTime()));

            plugin.getCoreProfileManager().exportToDatabase(profile, true, false);
        }

        plugin.getCoreProfileManager().getPermissionAttachments().remove(player.getUniqueId());

        if (plugin.getDisguiseManager().isDisguised(player)) {
            plugin.getDisguiseManager().undisguise(player, true);
        }

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

        if (plugin.getDisguiseManager().isDisguised(player)) {
            plugin.getDisguiseManager().undisguise(player, true);
        }
    }
}