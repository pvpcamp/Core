package camp.pvp.core.server;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CoreServer {

    private final String name, type;

    public CoreServer(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
