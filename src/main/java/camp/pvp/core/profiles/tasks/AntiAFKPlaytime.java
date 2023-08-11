package camp.pvp.core.profiles.tasks;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfileManager;
import camp.pvp.core.utils.Colors;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;


public class AntiAFKPlaytime implements Runnable, Listener {

    private Core plugin;
    private CoreProfileManager cpm;

    public AntiAFKPlaytime(Core plugin, CoreProfileManager cpm) {
        this.plugin = plugin;
        this.cpm = cpm;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(UUID.fromString("6559b156-7946-4e2c-a2cd-b1937ed4da67"))
                    || p.getUniqueId().equals(UUID.fromString("a2771454-4804-4ad4-aa2c-982ef2585f46"))
                    || p.getUniqueId().equals(UUID.fromString("772a6170-5a12-4874-b077-8efd8fb496fa"))
                    || p.getUniqueId().equals(UUID.fromString("fb68d569-f266-46bf-9bd7-3d160a788ce0")))
                return;
            else if (p.hasPermission("core.staff")) {
                if ((System.currentTimeMillis() - cpm.getLoadedProfiles().get(p.getUniqueId()).getAfk() > 300000)) {
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("KickPlayer");
                    out.writeUTF(p.getName());
                    out.writeUTF(Colors.get("&c&lSTAFF ONLY\n&cYou've been AFK for more than &l5 minutes&c.\n&cTo prevent staff members from abusing playtime afk you've been kicked."));

                    p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("core.staff")) {
            cpm.getLoadedProfiles().get(event.getPlayer().getUniqueId()).setAfk(System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().hasPermission("core.staff")) {
            cpm.getLoadedProfiles().get(event.getPlayer().getUniqueId()).setAfk(System.currentTimeMillis());
        }
    }
}
