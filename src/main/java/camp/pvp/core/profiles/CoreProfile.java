package camp.pvp.core.profiles;

import camp.pvp.core.Core;
import camp.pvp.core.chattags.ChatTag;
import camp.pvp.core.chattags.ChatTagManager;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.ranks.RankManager;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.*;

@Data
public class CoreProfile implements Comparable<CoreProfile>{

    private final UUID uuid;
    private String name, ip, authKey, timeZone;

    private List<String> ipList;

    private List<Rank> ranks;

    private ChatTag chatTag;
    private List<ChatTag> ownedChatTags;

    private ChatColor chatColor;

    private LobbyArmor appliedLobbyArmor;
    private List<LobbyArmor> ownedLobbyArmor;

    private FlightEffect appliedFlightEffect;
    private List<FlightEffect> ownedFlightEffects;

    private UUID replyTo;
    private List<UUID> ignored;

    private Map<String, Date> commandCooldowns;
    private Date chatCooldown;

    private String lastConnectedServer;
    private Date firstLogin, lastLogin, lastLogout;
    private long playtime, afk, lastLoadFromDatabase;

    private int flightEffectFrame;

    private boolean authenticated, namemc, seeGlobalChat, allowPrivateMessages, messageSounds, staffChat;

    public CoreProfile(UUID uuid) {
        this.uuid = uuid;
        this.ipList = new ArrayList<>();
        this.ranks = new ArrayList<>();
        this.ownedChatTags = new ArrayList<>();
        this.ignored = new ArrayList<>();
        this.commandCooldowns = new HashMap<>();

        this.seeGlobalChat = true;
        this.allowPrivateMessages = true;
        this.messageSounds = true;
        this.staffChat = false;

        this.appliedLobbyArmor = LobbyArmor.NONE;
        this.ownedLobbyArmor = new ArrayList<>();

        this.appliedFlightEffect = FlightEffect.NONE;
        this.ownedFlightEffects = new ArrayList<>();
    }

    public boolean canChat() {
        if(chatCooldown != null) {
            return chatCooldown.before(new Date());
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

    public void addCommandCooldown(String command, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, seconds);
        commandCooldowns.put(command, calendar.getTime());
    }

    public Set<Rank> getAllRanks() {
        Set<Rank> ranks = new HashSet<>();
        for(Rank rank : getRanks()) {
            ranks.add(rank);
            ranks.addAll(Core.getInstance().getRankManager().getAllParents(rank));
        }

        return ranks;
    }

    public Map<String, Boolean> getPermissions(String server) {
        Map<String, Boolean> permissions = new HashMap<>();
        for(Rank rank : getAllRanks()) {
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

    public boolean isCurrent() {
        Player player = getPlayer();
        return player != null || System.currentTimeMillis() - getLastLoadFromDatabase() < (1000 * 60);
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

    public List<Punishment> getPunishments() {
        return Core.getInstance().getPunishmentManager().getPunishmentsForPlayer(uuid);
    }

    public long getCurrentPlaytime() {
        if (Bukkit.getPlayer(getName()) != null && Bukkit.getPlayer(getName()).isOnline()) {
            return playtime + (new Date().getTime() - getLastLogin().getTime());
        } else {
            return playtime;
        }
    }

    public void addIp(String ip) {
        this.ip = ip;
        if(!ipList.contains(ip)) {
            ipList.add(ip);
        }
    }

    public void incrementFlightEffectFrame() {
        flightEffectFrame++;
    }

    public String convertToLocalTimeZone(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mm:ss a Z");
        sdf.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        return sdf.format(date);
    }

    public void importFromDocument(Core plugin, Document doc) {
        this.name = doc.getString("name");
        this.ip = doc.getString("ip");
        this.ipList = doc.getList("ip_list", String.class);
        this.timeZone = doc.get("time_zone", "America/New_York");
        this.firstLogin = doc.get("first_login", new Date());
        this.lastLogin = doc.get("last_login", new Date());
        this.lastLogout = doc.get("last_logout", new Date());
        this.lastConnectedServer = doc.get("last_connected_server", "none");
        this.playtime = doc.getLong("playtime");
        this.seeGlobalChat = doc.getBoolean("see_global_chat");
        this.allowPrivateMessages = doc.getBoolean("allow_private_messages");
        this.messageSounds = doc.getBoolean("message_sounds");
        this.staffChat = doc.getBoolean("staff_chat");
        this.namemc = doc.getBoolean("namemc");
        this.appliedLobbyArmor = LobbyArmor.valueOf(doc.get("applied_lobby_armor", "NONE"));
        this.appliedFlightEffect = FlightEffect.valueOf(doc.get("applied_flight_effect", "NONE"));

        this.chatTag = plugin.getChatTagManager().getChatTags().get(doc.get("applied_chat_tag", UUID.class));

        String chatColor = doc.getString("chat_color");
        if(chatColor != null) {
            this.chatColor = ChatColor.valueOf(chatColor);
        }

        if(doc.containsKey("auth_key")) {
            this.authKey = doc.getString("auth_key");
            this.authenticated = doc.getBoolean("authenticated");
        }

        for(String s : doc.getList("owned_flight_effects", String.class, new ArrayList<>())) {
            try {
                FlightEffect effect = FlightEffect.valueOf(s);
                ownedFlightEffects.add(effect);
            } catch (IllegalArgumentException ignored) {}
        }

        for(String s : doc.getList("owned_lobby_armor", String.class, new ArrayList<>())) {
            try {
                LobbyArmor armor = LobbyArmor.valueOf(s);
                ownedLobbyArmor.add(armor);
            } catch (IllegalArgumentException ignored) {}
        }

        ChatTagManager ctm = plugin.getChatTagManager();
        List<UUID> tagIds = doc.getList("owned_chat_tags", UUID.class, new ArrayList<>());
        for(UUID uuid : tagIds) {
            ChatTag tag = ctm.getChatTags().get(uuid);
            if(tag != null) {
                getOwnedChatTags().add(tag);
            }
        }

        RankManager rm = plugin.getRankManager();
        List<UUID> rankIds = doc.getList("ranks", UUID.class, new ArrayList<>());
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
        map.put("ip", getIp());
        map.put("ip_list", getIpList());
        map.put("time_zone", getTimeZone());
        map.put("first_login", getFirstLogin());
        map.put("last_login", getLastLogin());
        map.put("last_logout", getLastLogout());
        map.put("last_connected_server", getLastConnectedServer());
        map.put("playtime", getPlaytime());
        map.put("see_global_chat", isSeeGlobalChat());
        map.put("allow_private_messages", isAllowPrivateMessages());
        map.put("message_sounds", isMessageSounds());
        map.put("staff_chat", isStaffChat());
        map.put("namemc", isNamemc());
        map.put("auth_key", getAuthKey());
        map.put("authenticated", isAuthenticated());
        map.put("applied_lobby_armor", getAppliedLobbyArmor().name());
        map.put("applied_flight_effect", getAppliedFlightEffect().name());
        map.put("chat_color", getChatColor() == null ? null : getChatColor().name());

        List<String> ownedFlightEffects = new ArrayList<>();
        getOwnedFlightEffects().forEach(effect -> ownedFlightEffects.add(effect.name()));

        map.put("owned_flight_effects", ownedFlightEffects);

        List<String> ownedLobbyArmor = new ArrayList<>();
        getOwnedLobbyArmor().forEach(armor -> ownedLobbyArmor.add(armor.name()));

        map.put("owned_lobby_armor", ownedLobbyArmor);

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
