package camp.pvp.core.profiles;

import com.google.gson.*;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.UUID;

@Getter
public class MiniProfile {

    private final UUID uuid;
    private final String name, server;
    private final boolean staff;
    private final long retrievalTime;

    public MiniProfile(UUID uuid, String name, String server, boolean staff) {
        this.uuid = uuid;
        this.name = name;
        this.server = server;
        this.staff = staff;
        this.retrievalTime = System.currentTimeMillis();
    }

    public JsonElement serialize() {
        JsonObject object = new JsonObject();
        object.addProperty("uuid", uuid.toString());
        object.addProperty("name", name);
        object.addProperty("server", server);
        object.addProperty("staff", staff);
        return object;
    }

    public static MiniProfile deserialize(JsonElement element) {
        JsonObject object = element.getAsJsonObject();

        UUID uuid = UUID.fromString(object.get("uuid").getAsString());
        String name = object.get("name").getAsString();
        String server = object.get("server").getAsString();
        boolean staff = object.get("staff").getAsBoolean();

        return new MiniProfile(uuid, name, server, staff);
    }
}
