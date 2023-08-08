package io.github.nosequel.tab.shared;


import org.bukkit.Bukkit;

public class TabThread extends Thread {

    private TabHandler handler;

    /**
     * Constructor to make a new TabThread
     *
     * @param handler the handler to register it to
     */
    public TabThread(TabHandler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(this.handler::sendUpdate);
    }
}