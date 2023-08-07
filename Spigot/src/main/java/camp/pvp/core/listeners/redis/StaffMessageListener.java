package camp.pvp.core.listeners.redis;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.MiniProfile;
import camp.pvp.core.server.StaffMessageType;
import camp.pvp.core.utils.Colors;
import camp.pvp.redis.RedisSubscriberListener;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class StaffMessageListener implements RedisSubscriberListener {

    private SpigotCore plugin;
    private Map<UUID, MiniProfile> joinPlayers, leavePlayers;

    public StaffMessageListener(SpigotCore plugin) {
        this.plugin = plugin;
        this.joinPlayers = new HashMap<>();
        this.leavePlayers = new HashMap<>();

        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                List<UUID> remove = new ArrayList<>();
                long time = new Date().getTime();
                for(Map.Entry<UUID, MiniProfile> entry : joinPlayers.entrySet()) {
                    MiniProfile joinPlayer = entry.getValue();
                    MiniProfile leavePlayer = leavePlayers.get(joinPlayer.getUuid());

                    if(leavePlayer != null) {
                        if (time - joinPlayer.getDate().getTime() < 500 && time - leavePlayer.getDate().getTime() < 500) {
                            sendStaffMessage("&6[S] &f" + joinPlayer.getName() + "&6 joined &f" + joinPlayer.getServer() + "&6 from &f" + leavePlayer.getServer() + "&6.");
                            remove.add(joinPlayer.getUuid());
                        }
                    } else if (time - joinPlayer.getDate().getTime() > 300) {
                        sendStaffMessage("&6[S] &f" + joinPlayer.getName() + "&6 joined &f" + joinPlayer.getServer() + "&6.");
                        remove.add(joinPlayer.getUuid());
                    }
                }


                for(Map.Entry<UUID, MiniProfile> entry : leavePlayers.entrySet()) {
                    MiniProfile leavePlayer = entry.getValue();
                    MiniProfile joinPlayer = joinPlayers.get(leavePlayer.getUuid());

                    if(joinPlayer == null && time - leavePlayer.getDate().getTime() > 500) {
                        sendStaffMessage("&6[S] &f" + leavePlayer.getName() + "&6 left &f" + leavePlayer.getServer() + "&6.");
                        remove.add(leavePlayer.getUuid());
                    }
                }

                if(!remove.isEmpty()) {
                    for (UUID uuid : remove) {
                        joinPlayers.remove(uuid);
                        leavePlayers.remove(uuid);
                    }
                }
            }
        }, 1, 1);
    }

    @Override
    public void onReceive(JsonObject json) {
        StaffMessageType stm = StaffMessageType.valueOf(json.get("type").getAsString());
        switch(stm) {
            case MESSAGE:
                sendStaffMessage(json.get("message").getAsString());
                break;
            case JOIN:
                MiniProfile profile = new MiniProfile(UUID.fromString(json.get("uuid").getAsString()), json.get("name").getAsString(), json.get("server").getAsString());
                joinPlayers.put(profile.getUuid(), profile);
                break;
            case LEAVE:
                profile = new MiniProfile(UUID.fromString(json.get("uuid").getAsString()), json.get("name").getAsString(), json.get("server").getAsString());
                leavePlayers.put(profile.getUuid(), profile);
                break;
        }
    }

    public void sendStaffMessage(String message) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission("core.staff")) {
                player.sendMessage(Colors.get(message));
            }
        }
    }
}
