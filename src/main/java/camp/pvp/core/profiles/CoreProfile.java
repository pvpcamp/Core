package camp.pvp.core.profiles;

import camp.pvp.core.Core;
import camp.pvp.core.chattags.ChatTag;
import camp.pvp.core.chattags.ChatTagManager;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.punishments.PunishmentManager;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.ranks.RankManager;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter @Setter
public class CoreProfile implements Comparable<CoreProfile>{

    private final UUID uuid;
    private String name, ip, authKey;

    private List<String> ipList;

    private List<Rank> ranks;

    private ChatTag chatTag;
    private List<ChatTag> ownedChatTags;

    private List<Punishment> punishments;

    private UUID replyTo;
    private List<UUID> ignored;

    private Map<String, Date> commandCooldowns;
    private Date chatCooldown, disguiseCooldown;

    private Date firstLogin, lastLogin, lastLogout;
    private long playtime, afk;

    private boolean authenticated, namemc, seeGlobalChat, allowPrivateMessages, messageSounds, staffChat;

    public CoreProfile(UUID uuid) {
        this.uuid = uuid;
        this.ipList = new ArrayList<>();
        this.ranks = new ArrayList<>();
        this.ownedChatTags = new ArrayList<>();
        this.punishments = new ArrayList<>();
        this.ignored = new ArrayList<>();
        this.commandCooldowns = new HashMap<>();

        this.seeGlobalChat = true;
        this.allowPrivateMessages = true;
        this.messageSounds = true;
        this.staffChat = false;
    }

    public boolean canChat() {
        if(chatCooldown != null) {
            return chatCooldown.before(new Date());
        }
        return true;
    }

    public boolean canDisguise() {
        if (disguiseCooldown != null) {
            return disguiseCooldown.before(new Date());
        }
        return true;
    }

    public boolean canUseCommand(String command) {
        Date date = commandCooldowns.get(command);
        if(date != null) {
            return date.before(new Date());
        }

        return true;
    }

    public void addChatCooldown(int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, seconds);
        this.chatCooldown = calendar.getTime();
    }

    public void addDisguiseCooldown(int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, seconds);
        this.disguiseCooldown = calendar.getTime();
    }

    public void addCommandCooldown(String command, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, seconds);
        commandCooldowns.put(command, calendar.getTime());
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

            for(Rank pr : rank.getParents(Core.getInstance())) {
                for(Map.Entry<String, List<String>> entry : pr.getPermissions().entrySet()) {
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
        }

        permissions.put("minecraft.command.ban", false);
        permissions.put("minecraft.command.ban-ip", false);
        permissions.put("minecraft.command.kick", false);
        permissions.put("minecraft.command.list", false);
        permissions.put("minecraft.command.me", false);
        permissions.put("minecraft.command.say", false);
        permissions.put("minecraft.command.tell", false);

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

    public int getWeight() {
        return getHighestRank().getWeight();
    }

    public Punishment getActivePunishment(Punishment.Type type) {
        for(Punishment punishment : getPunishments()) {
            if(punishment.isActive() && punishment.getType().equals(type)) {
                return punishment;
            }
        }

        return null;
    }

    public long getCurrentPlaytime() {
        if (Bukkit.getPlayer(getName()) != null && Bukkit.getPlayer(getName()).isOnline()) {
            return playtime + (new Date().getTime() - getLastLogin().getTime());
        } else {
            return playtime;
        }
    }

    public void importFromDocument(Core plugin, Document doc) {
        this.name = doc.getString("name");
        this.ip = doc.getString("ip");
        this.ipList = doc.getList("ip_list", String.class);
        this.firstLogin = doc.getDate("first_login");
        this.lastLogin = doc.getDate("last_login");
        this.lastLogout = doc.getDate("last_logout");
        this.playtime = doc.getLong("playtime");
        this.seeGlobalChat = doc.getBoolean("see_global_chat");
        this.allowPrivateMessages = doc.getBoolean("allow_private_messages");
        this.messageSounds = doc.getBoolean("message_sounds");
        this.staffChat = doc.getBoolean("staff_chat");
        this.namemc = doc.getBoolean("namemc");

        this.chatTag = plugin.getChatTagManager().getChatTags().get(doc.get("applied_chat_tag", UUID.class));

        if(doc.containsKey("auth_key")) {
            this.authKey = doc.getString("auth_key");
            this.authenticated = doc.getBoolean("authenticated");
        }

        ChatTagManager ctm = plugin.getChatTagManager();
        List<UUID> tagIds = doc.getList("owned_chat_tags", UUID.class);
        for(UUID uuid : tagIds) {
            ChatTag tag = ctm.getChatTags().get(uuid);
            if(tag != null) {
                getOwnedChatTags().add(tag);
            }
        }

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
            if (punishment != null) {
                this.getPunishments().add(punishment);
            }
        }
    }

    public Map<String, Object> exportToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", getName());
        map.put("ip", getIp());
        map.put("ip_list", getIpList());
        map.put("first_login", getFirstLogin());
        map.put("last_login", getLastLogin());
        map.put("last_logout", getLastLogout());
        map.put("playtime", getPlaytime());
        map.put("see_global_chat", isSeeGlobalChat());
        map.put("allow_private_messages", isAllowPrivateMessages());
        map.put("message_sounds", isMessageSounds());
        map.put("staff_chat", isStaffChat());
        map.put("namemc", isNamemc());
        map.put("auth_key", getAuthKey());
        map.put("authenticated", isAuthenticated());

        UUID chatTag = getChatTag() == null ? null : getChatTag().getUuid();
        map.put("applied_chat_tag", chatTag);

        List<UUID> tagIds = new ArrayList<>();
        for(ChatTag tag : getOwnedChatTags()) {
            tagIds.add(tag.getUuid());
        }

        map.put("owned_chat_tags", tagIds);

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

    @Override
    public int compareTo(CoreProfile o) {
        int weight = o.getWeight() - this.getWeight();

        if(weight == 0) {
            weight = this.getName().compareTo(o.getName());
        }
        return weight;
    }
}
