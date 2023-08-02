package camp.pvp.core.tablist;

import camp.pvp.core.ranks.Rank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class TabPlayer {
    @Getter private String name;
    @Getter private Rank rank;
}