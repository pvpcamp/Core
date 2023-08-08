package camp.pvp.core.server;

import camp.pvp.core.Core;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatAnnouncer implements Runnable{

    private Core plugin;
    private int line;
    private List<String> announcements;
    public ChatAnnouncer(Core plugin) {
        this.plugin = plugin;
        this.line = 0;
        this.announcements = plugin.getConfig().getStringList("messages.announcements");
    }

    @Override
    public void run() {
        if(announcements.size() == line) {
            line = 0;
        }

        final String announcement = announcements.get(line);
        StringBuilder sb = new StringBuilder();
        sb.append(" \n");
        sb.append(announcement);
        sb.append("\n ");

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Colors.get(sb.toString()));
        }

        line++;
    }
}
