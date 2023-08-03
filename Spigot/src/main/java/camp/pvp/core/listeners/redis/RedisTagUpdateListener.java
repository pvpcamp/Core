package camp.pvp.core.listeners.redis;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.chattags.ChatTag;
import camp.pvp.core.chattags.ChatTagManager;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.profiles.CoreProfileManager;
import camp.pvp.core.ranks.Rank;
import camp.pvp.redis.RedisSubscriberListener;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class RedisTagUpdateListener implements RedisSubscriberListener {

    private SpigotCore plugin;
    private ChatTagManager chatTagManager;
    public RedisTagUpdateListener(SpigotCore plugin, ChatTagManager chatTagManager) {
        this.plugin = plugin;
        this.chatTagManager = chatTagManager;
    }

    @Override
    public void onReceive(JsonObject json) {
        UUID uuid = UUID.fromString(json.get("uuid").getAsString());
        String fromServer = json.get("from_server").getAsString();
        boolean deleted = json.get("deleted").getAsBoolean();

        if(!chatTagManager.getPlugin().getCoreServerManager().getCoreServer().getName().equals(fromServer)) {
            String message;
            if(deleted) {
                final ChatTag tag = chatTagManager.getChatTags().get(uuid);
                chatTagManager.getChatTags().remove(uuid);
                message = "&cChat Tag &f" + tag.getName() + " &deleted from &f" + fromServer + "&c.";
            } else {
                ChatTag tag = chatTagManager.importFromDatabase(uuid);
                message = "&cChat Tag &f" + tag.getName() + " &cupdated from &f" + fromServer + "&c.";
            }

            plugin.getCoreProfileManager().staffBroadcast(message);
        }
    }
}
