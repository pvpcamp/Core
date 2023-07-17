package camp.pvp.core.profiles;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.chattags.ChatTag;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.ranks.RankManager;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.*;

@Getter @Setter
public class CoreProfile {

    private final UUID uuid;
    private String name;

    private List<Rank> ranks;
    private Map<String, Map<String, Boolean>> permissions;

    private ChatTag chatTag;
    private List<ChatTag> ownedChatTags;

    public CoreProfile(UUID uuid) {
        this.uuid = uuid;
        this.ranks = new ArrayList<>();
    }

    public Rank getHighestRank() {
        Rank rank = null;
        for(Rank r : getRanks()) {
            if(rank != null) {
                if(rank.getWeight() < r.getWeight()) {
                    rank = r;
                }
            } else {
                rank = r;
            }
        }

        return rank;
    }

    public void importFromDocument(SpigotCore plugin, Document doc) {
        this.name = doc.getString("name");

        RankManager rm = plugin.getRankManager();
        List<UUID> rankIds = doc.getList("ranks", UUID.class);
        for(UUID uuid : rankIds) {
            Rank rank = rm.getRanks().get(uuid);
            if(rank != null) {
                getRanks().add(rank);
            }
        }
    }

    public Map<String, Object> exportToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", getName());

        List<UUID> rankIds = new ArrayList<>();
        for(Rank rank : getRanks()) {
            rankIds.add(rank.getUuid());
        }

        map.put("ranks", rankIds);


        return map;
    }
}
