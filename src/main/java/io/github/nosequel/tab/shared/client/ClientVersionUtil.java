package io.github.nosequel.tab.shared.client;

import com.viaversion.viaversion.api.Via;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class ClientVersionUtil {

    public static int getProtocolVersion(Player player) {
        PluginManager pluginManager = Bukkit.getPluginManager();

        if(pluginManager.getPlugin("ViaVersion") != null) {
            return Via.getAPI().getPlayerVersion(player.getUniqueId());
        }

        return -1;
    }

}
