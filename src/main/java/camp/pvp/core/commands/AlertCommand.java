package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class AlertCommand {

    @Command(name = "alert", aliases = {"broadcast"}, permission = "core.commands.alert")
    public void alert(CommandArgs args) {
        if (args.length() == 0) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <message>");
            return;
        }
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < args.length(); i++) {
            message.append(args.getArgs(i));
            if (i + 1 != args.length()) {
                message.append(" ");
            }
        }
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendMessage(Colors.get("&8[&4Alert&8] &f" + message));
        });
    }
}