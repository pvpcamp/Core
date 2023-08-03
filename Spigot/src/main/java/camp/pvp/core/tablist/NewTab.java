package camp.pvp.core.tablist;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.profiles.CoreProfileManager;
import camp.pvp.core.utils.Colors;
import io.github.nosequel.tab.shared.client.ClientVersionUtil;
import io.github.nosequel.tab.shared.entry.TabElement;
import io.github.nosequel.tab.shared.entry.TabElementHandler;
import io.github.nosequel.tab.shared.skin.SkinType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NewTab implements TabElementHandler {

    @Override
    public TabElement getElement(Player player) {

        TabElement tabElement = new TabElement();
        tabElement.setHeader(ChatColor.GOLD + "Practice");
        tabElement.setFooter(ChatColor.GOLD + "pvp.camp");

        List<TabPlayer> tabPlayerList = new ArrayList<>();

        CoreProfileManager coreProfileManager = SpigotCore.getInstance().getCoreProfileManager();

        Bukkit.getOnlinePlayers().forEach(p -> {
            CoreProfile coreProfile = coreProfileManager.getLoadedProfiles().get(p.getUniqueId());
            tabPlayerList.add(new TabPlayer(p.getName(), coreProfile.getHighestRank()));
        });

        int version = ClientVersionUtil.getProtocolVersion(player);

        if(version == 5) {
            for (int i = 0; i < 60; i++) {
                final int x = i % 3;
                final int y = i / 3;
                updateTabElement(tabElement, tabPlayerList, i, x, y);
            }
        } else {
            for (int i = 0; i < 80; i++) {
                final int x = i % 4;
                final int y = i / 4;
                updateTabElement(tabElement, tabPlayerList, i, x, y);
            }
        }
        return tabElement;
    }

    private void updateTabElement(TabElement tabElement, List<TabPlayer> tabPlayerList, int i, int x, int y) {
        if (tabPlayerList.size() > i) {
            TabPlayer tabPlayer = tabPlayerList.get(i);
            Player p = Bukkit.getPlayer(tabPlayer.getName());
            tabElement.add(x, y, Colors.get(tabPlayer.getRank().getColor() + tabPlayer.getName()), ((CraftPlayer) p).getHandle().ping, SkinType.fromUsername(p.getName()));
        } else {
            tabElement.add(x, y, " ");
        }
    }
}
