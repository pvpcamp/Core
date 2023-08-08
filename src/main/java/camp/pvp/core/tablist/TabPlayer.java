package camp.pvp.core.tablist;

import camp.pvp.core.ranks.Rank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TabPlayer {
    private String name;
    private Rank rank;
}