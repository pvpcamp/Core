package camp.pvp.core.chattags;

import camp.pvp.core.SpigotCore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatTagManager {

    private SpigotCore plugin;
    private Map<UUID, ChatTag> chatTags;
    public ChatTagManager(SpigotCore plugin) {
        this.plugin = plugin;
        this.chatTags = new HashMap<>();

        plugin.getLogger().info("Started ChatTagManager.");
    }
}
