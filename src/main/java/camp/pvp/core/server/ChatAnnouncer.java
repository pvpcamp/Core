package camp.pvp.core.server;

import camp.pvp.core.Core;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatAnnouncer implements Runnable{

    private Core plugin;
    private int line;
    private List<String[]> announcements;
    public ChatAnnouncer(Core plugin) {
        this.plugin = plugin;
        this.line = 0;

        plugin.getConfig().getConfigurationSection("messages.announcements").getKeys(false).forEach(key -> {
            announcements.add(plugin.getConfig().getStringList("messages.announcements." + key).toArray(new String[0]));
        });
    }

    @Override
    public void run() {
        if(announcements.size() == line) {
            line = 0;
        }

        String[] announcement = announcements.get(line);
        for(int i = 0; i < announcement.length; i++) {
            announcement[i] = Colors.get(announcement[i]);
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(announcement);
        }

        line++;
    }
}
