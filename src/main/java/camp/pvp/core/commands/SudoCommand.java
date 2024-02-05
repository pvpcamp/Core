package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoCommand implements CommandExecutor {

    private Core plugin;
    public SudoCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getCommand("sudo").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            return true;
        }

        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + command.getLabel() + " <player> <message/command>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null) {
            sender.sendMessage(ChatColor.RED + "The target you specified is not on this server.");
            return true;
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

        return true;
    }
}
