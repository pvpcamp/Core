package camp.pvp.core.listeners.player;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.chattags.ChatTag;
import camp.pvp.core.profiles.ChatHistory;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.utils.Colors;
import camp.pvp.core.utils.DateUtils;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Date;
import java.util.UUID;

public class PlayerChatListener implements Listener {

    private SpigotCore plugin;
    public PlayerChatListener(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(profile != null && !event.isCancelled()) {
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

            StringBuilder chatFormat = new StringBuilder();

            if(rank.getPrefix() != null) {
                chatFormat.append(rank.getPrefix() + " ");
            }

            chatFormat.append(rank.getColor() + player.getName());

            if(tag != null) {
                chatFormat.append(" " + tag);
            }

            chatFormat.append("&7:&f %2$s");

            event.setFormat(Colors.get(chatFormat.toString()));

            if(!profile.isStaffChat()) {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    CoreProfile pr = plugin.getCoreProfileManager().getLoadedProfiles().get(p.getUniqueId());
                    if(pr != null) {
                        if(!pr.isSeeGlobalChat()) {
                            event.getRecipients().remove(p);
                        }

                        if(pr.getIgnored().contains(player.getUniqueId())) {
                            event.getRecipients().remove(p);
                        }
                    }
                }

                ChatHistory chatHistory = new ChatHistory(
                        UUID.randomUUID(),
                        player.getUniqueId(),
                        player.getName(),
                        event.getMessage(),
                        plugin.getCoreServerManager().getCoreServer().getName(),
                        ChatHistory.Type.PUBLIC_CHAT,
                        new Date(),
                        false);

                plugin.getCoreProfileManager().exportHistory(chatHistory, true);
            } else {
                JsonObject json = new JsonObject();
                json.addProperty("message",  "&5[Staff Chat] &7(" + plugin.getCoreServerManager().getCoreServer().getName() + "&7) " + profile.getHighestRank().getColor() + profile.getName() + "&7: &f" + event.getMessage());
                plugin.getNetworkHelper().getRedisPublisher().publishMessage("core_staff", json);
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Your profile has not been loaded yet.");
        }
    }
}
