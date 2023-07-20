package camp.pvp.core.events;

import camp.pvp.utils.guis.Gui;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Date;
import java.util.UUID;

@Getter
public class MongoGuiEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public MongoGuiEvent(Gui gui, UUID uniqueId, Date requestStarted) {
        this.gui = gui;
        this.uniqueId = uniqueId;
        this.requestStarted = requestStarted;
        this.requestFinished = new Date();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uniqueId);
    }

    public long getRequestTime() {
        return requestFinished.getTime() - requestStarted.getTime();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    private final Gui gui;
    private final UUID uniqueId;
    private final Date requestStarted, requestFinished;
}
