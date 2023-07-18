package camp.pvp.core.profiles;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.chattags.ChatTag;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.punishments.PunishmentManager;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.ranks.RankManager;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

import java.util.*;

@Getter @Setter
public class CoreProfile {

    private final UUID uuid;
    private String name;

    private List<Rank> ranks;

    private ChatTag chatTag;
    private List<ChatTag> ownedChatTags;

    private List<Punishment> punishments;

    public CoreProfile(UUID uuid) {
        this.uuid = uuid;
        this.ranks = new ArrayList<>();
    }

    public Map<String, Boolean> getPermissions(String server) {
        Map<String, Boolean> permissions = new HashMap<>();
        for(Rank rank : getRanks()) {
            for(Map.Entry<String, List<String>> entry : rank.getPermissions().entrySet()) {
                if(entry.getKey().equalsIgnoreCase("_global") || entry.getKey().equalsIgnoreCase(server)) {
                    List<String> permList = entry.getValue();
                    if(permList != null) {
                        for (String s : permList) {
                            permissions.put(s, true);
                        }
                    }
                }
            }
        }

        return permissions;
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(this.getUuid());
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

    public Punishment getActivePunishment(Punishment.Type type) {
        for(Punishment punishment : getPunishments()) {
            if(punishment.isActive() && punishment.getType().equals(type)) {
                return punishment;
            }
        }

        return null;
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

        PunishmentManager pm = plugin.getPunishmentManager();
        List<UUID> punishmentIds = doc.getList("punishments", UUID.class);
        for(UUID uuid : punishmentIds) {
            Punishment punishment = pm.importFromDatabase(uuid);
            getPunishments().add(punishment);
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

        List<UUID> punishmentIds = new ArrayList<>();
        for(Punishment punishment : getPunishments()) {
            punishmentIds.add(punishment.getUuid());
        }

        map.put("punishments", punishmentIds);


        return map;
    }
}
