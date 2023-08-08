package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.Core;
import camp.pvp.core.server.CoreServer;
import camp.pvp.core.utils.Colors;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ServerInfoCommand {

    private Core plugin;
    public ServerInfoCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Command(name = "serverinfo",
            permission = "core.commands.serverinfo",
            description = "View the basic information of a remote server.")
    public void serverInfo(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();
        if(args.length > 0) {
            CoreServer server = plugin.getCoreServerManager().findServer(args[0]);
            if(server == null) {
                sender.sendMessage(ChatColor.RED + "The server you specified was not found.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("&6Server: &f" + server.getName());
            sb.append("\n&6Type: &f" + server.getType());
            sb.append("\n&6Players: &f" + server.getOnline() + "/" + server.getSlots());
            sb.append(server.isCurrentlyOnline() ? "\n&eThis server is online." : "\n&cThis server is currently offline.");

            sender.sendMessage(Colors.get(sb.toString()));
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /serverinfo <server>");
        }
    }
}
