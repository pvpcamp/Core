package camp.pvp.core.tablist;

import org.bukkit.entity.Player;

import java.util.Map;

public interface Tablist {

    String getHeader();
    String getFooter();
    Map<Integer, String> getSlots(Player player);
}
