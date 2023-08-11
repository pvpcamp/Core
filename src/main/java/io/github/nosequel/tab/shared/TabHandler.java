package io.github.nosequel.tab.shared;

import io.github.nosequel.tab.shared.entry.TabElement;
import io.github.nosequel.tab.shared.entry.TabElementHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class TabHandler {

    private TabAdapter adapter;
    private TabElementHandler handler;

    private long ticks;

    /**
     * Constructor to make a new tab handler
     *
     * @param adapter the adapter to send the tab with
     * @param handler the handler to get the elements from
     * @param plugin  the plugin to register the thread to
     * @param ticks   the amount it should update
     */
    public TabHandler(TabAdapter adapter, TabElementHandler handler, JavaPlugin plugin, long ticks) {
        this.adapter = adapter;
        this.handler = handler;
        this.ticks = ticks;

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new TabRunnable(this), 1L, ticks);
    }

    /**
     * Update the tablist for a player
     *
     * @param player the player to update it for
     */
    public void sendUpdate(Player player) {
        TabElement tabElement = this.handler.getElement(player);

        this.adapter
                .setupProfiles(player)
                .addFakePlayers(player)
                .hideRealPlayers(player).handleElement(player, tabElement)
                .sendHeaderFooter(player, tabElement.getHeader(), tabElement.getFooter());
    }

}