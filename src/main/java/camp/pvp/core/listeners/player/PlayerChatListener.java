package camp.pvp.core.listeners.player;

import camp.pvp.core.Core;
import camp.pvp.core.chattags.ChatTag;
import camp.pvp.core.profiles.ChatHistory;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.utils.ChatUtils;
import camp.pvp.core.utils.Colors;
import camp.pvp.core.utils.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Date;
import java.util.UUID;

public class PlayerChatListener implements Listener {

    private Core plugin;
    public PlayerChatListener(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(profile != null && profile.isLoaded() && !event.isCancelled()) {

            if(profile.getAuthKey() != null && !profile.isAuthenticated()) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You are not authenticated.");
                return;
            }

            Punishment mute = profile.getActivePunishment(Punishment.Type.MUTE);
            if(mute != null) {
                player.sendMessage(Colors.get(mute.getType().getMessage()));
                player.sendMessage(Colors.get("&cReason: &f" + mute.getReason()));
                player.sendMessage(Colors.get("&cExpires: &f" + (mute.getExpires() == null ? "Never" : DateUtils.getDifference(mute.getExpires(), new Date()))));
                player.sendMessage(Colors.get(mute.getType().getAppealMessage()));
                event.setCancelled(true);
                return;
            }

            if(!profile.isSeeGlobalChat()) {
                player.sendMessage(ChatColor.RED + "You currently have global chat disabled.");
                event.setCancelled(true);
                return;
            }

            Rank rank = profile.getHighestRank();
            ChatTag tag = profile.getChatTag();

            if (plugin.getCoreServerManager().getCoreServer().isMutedChat() && !player.hasPermission("core.staff")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Global Chat is currently muted.");
                return;
            }

            event.setFormat(Colors.get(format(player, rank, tag)));

            if(!profile.isStaffChat()) {

                if(profile.canChat()) {

                    boolean filtered = ChatUtils.isFiltered(event.getMessage());

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        CoreProfile pr = plugin.getCoreProfileManager().getLoadedProfiles().get(p.getUniqueId());
                        if (pr != null) {
                            if (!pr.isSeeGlobalChat()) {
                                event.getRecipients().remove(p);
                            }

                            if (pr.getIgnored().contains(player.getUniqueId()) || (filtered && !player.hasPermission("core.chat.bypass.filter"))) {
                                if(p != player) {
                                    event.getRecipients().remove(p);
                                }
                            }
                        }
                    }

                    if(filtered) {
                        if(player.hasPermission("core.chat.bypass.filter")) {
                            player.sendMessage(ChatColor.RED + "Your message would have been filtered.");
                        } else {
                            plugin.getCoreServerManager().sendStaffMessage("&c[Filtered] &7(" + plugin.getCoreServerManager().getCoreServer().getName() + "&7) &f" + player.getName() + "&7: &f" + event.getMessage());
                        }
                    }
                    String name = player.getName();

                    ChatHistory chatHistory = new ChatHistory(
                            UUID.randomUUID(),
                            player.getUniqueId(),
                            name,
                            event.getMessage(),
                            plugin.getCoreServerManager().getCoreServer().getName(),
                            ChatHistory.Type.PUBLIC_CHAT,
                            new Date(),
                            filtered);
                    plugin.getCoreProfileManager().exportHistory(chatHistory, true);

                    if(!player.hasPermission("core.chat.bypass.cooldown")) {
                        profile.addChatCooldown(plugin.getConfig().getInt("chat.cooldown"));
                    }
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("&cYou must wait " + DateUtils.getTimeUntil(profile.getChatCooldown()) + " before using global chat again.");
                    sb.append("\n&cIf you would like to bypass this cooldown, please purchase &c&lPlus Rank &r&cor higher here: &fstore.pvp.camp");
                    player.sendMessage(Colors.get(sb.toString()));
                    event.setCancelled(true);
                }
            } else {
                plugin.getCoreServerManager().sendStaffMessage("&5[SC] &7[" + plugin.getCoreServerManager().getCoreServer().getName() + "&7] " + rank.getColor() + profile.getName() + "&7: &f" + event.getMessage());
                event.setCancelled(true);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Your profile has not been loaded yet. If this persists, please reconnect.");
            event.setCancelled(true);
        }
    }

    public String format(Player player, Rank rank, ChatTag tag) {
        StringBuilder chatFormat = new StringBuilder();

        if (rank == null) {
            rank = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId()).getHighestRank();
        }
        if(rank.getPrefix() != null) {
            chatFormat.append(rank.getPrefix() + " ");
        }

        chatFormat.append(rank.getColor() + player.getName());

        if(tag != null) {
            chatFormat.append(" &f" + tag.getTag());
        }

        chatFormat.append("&7:&f %2$s");
        return chatFormat.toString();
    }
}
