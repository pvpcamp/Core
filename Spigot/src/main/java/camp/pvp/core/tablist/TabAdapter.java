package camp.pvp.core.tablist;

import io.github.thatkawaiisam.ziggurat.ZigguratAdapter;
import io.github.thatkawaiisam.ziggurat.ZigguratCommons;
import io.github.thatkawaiisam.ziggurat.utils.BufferedTabObject;
import io.github.thatkawaiisam.ziggurat.utils.TabColumn;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class TabAdapter implements ZigguratAdapter {

    public Tablist tablist;

    @Override
    public String getHeader() {
        return tablist.getHeader();
    }

    @Override
    public String getFooter() {
        return tablist.getFooter();
    }

    @Override
    public Set<BufferedTabObject> getSlots(Player player) {
        HashSet<BufferedTabObject> bufferedTabObjects = new HashSet<>();

        for (int slot : tablist.getSlots(player).keySet()) {

            TabColumn tabColumn = null;
            String text = tablist.getSlots(player).get(slot);

            if (slot >= 1 && slot <= 20) {
                tabColumn = TabColumn.LEFT;
            } else if (slot > 20 && slot <= 40) {
                tabColumn = TabColumn.MIDDLE;
            } else if (slot >= 41 && slot <= 60) {
                tabColumn = TabColumn.RIGHT;
            } else if (slot >= 61) {
                tabColumn = TabColumn.FAR_RIGHT;
            }

            int newSlot = slot;

            switch (tabColumn) {
                case MIDDLE: {
                    newSlot -= 20;
                    break;
                }
                case RIGHT: {
                    newSlot -= 40;
                    break;
                }
                case FAR_RIGHT: {
                    newSlot -= 60;
                    break;
                }
            }

            bufferedTabObjects.add(new BufferedTabObject().column(tabColumn).slot(newSlot).ping(1).text(text).skin(ZigguratCommons.defaultTexture));
        }
        return bufferedTabObjects;
    }
}
