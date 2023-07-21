package camp.pvp.core.profiles;

import camp.pvp.core.utils.DateUtils;
import lombok.Getter;
import org.bson.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class ChatHistory implements Comparable<ChatHistory> {

    public ChatHistory(UUID uuid, UUID player, String playerName, String chat, String server, Type type, Date date, boolean filtered) {
        this.uuid = uuid;
        this.player = player;
        this.playerName = playerName;
        this.chat = chat;
        this.server = server;
        this.type = type;
        this.date = date;
        this.filtered = filtered;
    }

    public ChatHistory(Document doc) {
        this.uuid = doc.get("_id", UUID.class);
        this.player = doc.get("player", UUID.class);
        this.playerName = doc.getString("player_name");
        this.chat = doc.getString("chat");
        this.server = doc.getString("server");
        this.type = Type.valueOf(doc.getString("type"));
        this.date = doc.getDate("date");
        this.filtered = doc.getBoolean("filtered");
    }

    public Map<String, Object> exportToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("player", player);
        map.put("player_name", playerName);
        map.put("chat", chat);
        map.put("server", server);
        map.put("type", type.name());
        map.put("date", date);
        map.put("filtered", filtered);

        return map;
    }

    public String getMessage() {
        switch(type) {
            case COMMAND:
                return "&6[/] &f" + getChat() + " &7" + DateUtils.getDifference(new Date(), getDate());
            case PRIVATE_MESSAGE:
                return "&9[PM] &fTo " + getChat() + (isFiltered() ? "&c [Filtered]" : "") + " &7" + DateUtils.getDifference(new Date(), getDate());
            case PUBLIC_CHAT:
                return "&a[G] &f" + getChat() + (isFiltered() ? "&c [Filtered]" : "") + " &7" + DateUtils.getDifference(new Date(), getDate());
        }
        return null;
    }

    @Override
    public int compareTo(ChatHistory ch) {
        return ch.getDate().compareTo(this.getDate());
    }

    public enum Type {
        COMMAND, PRIVATE_MESSAGE, PUBLIC_CHAT;

        public static Type fromString(String s) {
            switch(s.toLowerCase()) {
                case "c":
                case "cmd":
                case "command":
                case "commands":
                    return COMMAND;
                case "m":
                case "pm":
                case "msg":
                case "message":
                case "messages":
                    return PRIVATE_MESSAGE;
                case "g":
                case "gc":
                case "chat":
                case "global":
                case "globalchat":
                    return PUBLIC_CHAT;
                default:
                    return null;
            }
        }
    }

    private final UUID uuid, player;
    private final String playerName, chat, server;
    private final Type type;
    private final Date date;
    private final boolean filtered;

}
