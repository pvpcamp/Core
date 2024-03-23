package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.guis.servers.ServersGui;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ServersCommand implements CommandExecutor {

    private Core plugin;
    public ServersCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("servers").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)) return true;

        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfile(player.getUniqueId());
        new ServersGui(profile).open(player);

        return true;
    }
}
