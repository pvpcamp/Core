package camp.pvp.core.listeners.redis;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.ranks.RankManager;
import camp.pvp.redis.RedisSubscriberListener;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RedisRankUpdateListener implements RedisSubscriberListener {

    private Core plugin;
    private RankManager rankManager;
    public RedisRankUpdateListener(Core plugin, RankManager rankManager) {
        this.plugin = plugin;
        this.rankManager = rankManager;
    }

    @Override
    public void onReceive(JsonObject json) {
        UUID uuid = UUID.fromString(json.get("uuid").getAsString());
        String fromServer = json.get("from_server").getAsString();
        boolean deleted = json.get("deleted").getAsBoolean();

        if(!rankManager.getPlugin().getCoreServerManager().getCoreServer().getName().equals(fromServer)) {
            String message;
            if(deleted) {
                final Rank rank = rankManager.getRanks().get(uuid);
                rankManager.getRanks().remove(uuid);

                for(Player player : Bukkit.getOnlinePlayers()) {
                    CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
                    boolean b = profile.getRanks().remove(rank);
                    if(b) {
                        plugin.getCoreProfileManager().updatePermissions(profile);
                    }
                }

                message = "&cRank &f" + rank.getName() + " &deleted from &f" + fromServer + "&c.";

            } else {
                Rank rank = rankManager.importFromDatabase(uuid);
                message = "&cRank &f" + rank.getName() + " &cupdated from &f" + fromServer + "&c.";
            }

            plugin.getCoreProfileManager().staffBroadcast(message);
        }
    }
}
