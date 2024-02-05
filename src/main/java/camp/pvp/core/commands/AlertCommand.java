package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AlertCommand implements CommandExecutor {

    private Core plugin;
    public AlertCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("alert").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <message>");
            return true;
        }

        StringBuilder message = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            message.append(args[i]);
            if (i + 1 != args.length) {
                message.append(" ");
            }
        }

        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendMessage(Colors.get("&8[&4Alert&8] &f" + message));
        });

        return true;
    }
}