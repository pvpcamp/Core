package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.server.CoreServer;
import camp.pvp.core.utils.Colors;
import camp.pvp.core.utils.DateUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Date;

public class ServerInfoCommand implements CommandExecutor {

    private Core plugin;
    public ServerInfoCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getCommand("serverinfo").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /serverinfo <server>");
            return true;
        }

        CoreServer server = plugin.getCoreServerManager().findServer(args[0]);
        if(server == null) {
            sender.sendMessage(ChatColor.RED + "The server you specified was not found.");
            return true;
        }

        Date startTime = new Date();
        startTime.setTime(server.getUpTime());

        StringBuilder sb = new StringBuilder();
        sb.append("&6Server: &f" + server.getName());
        sb.append("\n&6Type: &f" + server.getType());

        if(server.isCurrentlyOnline()) {
            sb.append("\n&6Uptime: &f" + DateUtils.getDifference(new Date(), startTime));
            sb.append("\n&6Players: &f" + server.getOnline() + "/" + server.getSlots());
        }

        sb.append(server.isCurrentlyOnline() ? "\n&eThis server is online." : "\n&cThis server is currently offline.");

        sender.sendMessage(Colors.get(sb.toString()));

        return true;
    }
}
