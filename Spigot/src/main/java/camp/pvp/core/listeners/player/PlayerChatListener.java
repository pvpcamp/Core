package camp.pvp.core.listeners.player;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.chattags.ChatTag;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.utils.Colors;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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

        if(profile != null) {
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
        } else {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Your profile has not been loaded yet.");
        }
    }
}
