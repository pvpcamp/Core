package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.Core;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoCommand {

    private Core plugin;
    public SudoCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Command(name = "sudo",
            permission = "core.commands.sudo",
            description = "Force a player to chat or perform a command.")
    public void sudo(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        CommandSender sender = commandArgs.getSender();

        if(args.length > 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                sender.sendMessage(ChatColor.RED + "The player you specified is not on this server.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for(int i = 1; i < args.length; i++) {
                sb.append(args[i]);
                if(i + 1 != args.length) {
                    sb.append(" ");
                }
            }

            String message = sb.toString();
            if(message.startsWith("/")) {
                target.performCommand(message.substring(1));
                sender.sendMessage(Colors.get("&aYou made &f" + target.getName() + " &aexecute command: &f" + message));
            } else {
                target.chat(message);
                sender.sendMessage(Colors.get("&aYou made &f" + target.getName() + " &asend chat: &f" + message));
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + commandArgs.getLabel() + " <player> <message/command>");
        }
    }
}
