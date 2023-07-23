package camp.pvp.core.commands.users;

import camp.pvp.core.SpigotCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpOpCommand implements CommandExecutor {

    private SpigotCore plugin;
    public HelpOpCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("helpop").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length > 0) {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < args.length; i++) {
                sb.append(args[i]);
                if(i + 1 != args.length) {
                    sb.append(" ");
                }

                plugin.getCoreServerManager().sendStaffMessage("&d[Request] &f" + sender.getName() + "&d sent a new request from server &f" + plugin.getCoreServerManager().getCoreServer().getName() + "&d: &f" + sb.toString());
                sender.sendMessage(ChatColor.GREEN + "Your request has been sent to all online staff members.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <request>");
        }

        return true;
    }
}
