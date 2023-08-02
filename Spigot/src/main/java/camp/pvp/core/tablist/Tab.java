package camp.pvp.core.tablist;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.profiles.CoreProfileManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tab implements Tablist {

    private SpigotCore plugin;

    public Tab(SpigotCore plugin) {
        this.plugin = plugin;
    }

    public String getHeader() {
        return null;
    }

    public String getFooter() {
        return null;
    }

    public Map<Integer, String> getSlots(Player player) {

        Map<Integer, String> slots = new HashMap<>();
        List<TabPlayer> tabPlayerList = new ArrayList<>();

        CoreProfileManager coreProfileManager = plugin.getCoreProfileManager();
        CoreProfile coreProfile = coreProfileManager.find(player.getName(), false);

        tabPlayerList.add(new TabPlayer(player.getName(), coreProfile.getHighestRank()));

        int left = 0;
        int middle = 0;
        int right = 0;
        String lastColumn = "r";

        for (TabPlayer tabPlayer : tabPlayerList) {
            String newColumn = "";

            int slot = 0;

            switch (lastColumn) {
                case "r": {
                    newColumn = "l";
                    left += 1;

                    slot = left;
                    break;
                }
                case "l": {
                    newColumn = "m";
                    middle += 1;

                    slot = 20 + middle;
                    break;
                }
                case "m": {
                    newColumn = "r";
                    right += 1;

                    slot = 40 + right;
                    break;
                }
            }
            CoreProfile tabProfile = coreProfileManager.find(tabPlayer.getName(), false);
            slots.put(slot, tabProfile.getHighestRank().getColor() + tabPlayer.getName());
            lastColumn = newColumn;
        }
        return slots;
    }
}
