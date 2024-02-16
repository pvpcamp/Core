package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.guis.cosmetics.FlightEffectsGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlightEffectsCommand implements CommandExecutor {

    private Core plugin;
    public FlightEffectsCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("flighteffects").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)) return true;
        new FlightEffectsGui(player).open(player);

        return true;
    }
}
