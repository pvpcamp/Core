package camp.pvp.core.listeners.packets;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerInfoListener extends PacketAdapter {

    private Core plugin;

    public PlayerInfoListener(Core plugin) {
        super(plugin, PacketType.Play.Server.PLAYER_INFO);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();

        List<PlayerInfoData> newPlayerInfoData = new ArrayList<>();
        for(PlayerInfoData pid : packet.getPlayerInfoDataLists().read(0)) {

            if(pid == null || pid.getProfile() == null || Bukkit.getPlayer(pid.getProfileId()) == null) {
                newPlayerInfoData.add(pid);
                continue;
            }

            UUID uuid = pid.getProfileId();
            Player player = Bukkit.getPlayer(uuid);

            if(!player.isOnline()) {
                newPlayerInfoData.add(pid);
                continue;
            }

            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(uuid);

            String color = profile.getHighestRank().getColor();
            color = color.length() < 2 ? color : color.substring(0, 2);

            String name = profile.getName();
            name = name.length() < 14 ? name : name.substring(0, 14);

            newPlayerInfoData.add(new PlayerInfoData(pid.getProfile(), pid.getLatency(), pid.getGameMode(), WrappedChatComponent.fromText(Colors.get(color + name))));

            if(packet.getPlayerInfoAction().read(0) == EnumWrappers.PlayerInfoAction.REMOVE_PLAYER && !event.getPlayer().canSee(player)) {
                PacketContainer p = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
                p.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                p.getPlayerInfoDataLists().write(0, newPlayerInfoData);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    try {
                        plugin.getProtocolManager().sendServerPacket(event.getPlayer(), p);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 1);
            }
        }

        packet.getPlayerInfoDataLists().write(0, newPlayerInfoData);
    }
}
