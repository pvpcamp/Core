package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpOpCommand implements CommandExecutor {

    private Core plugin;
    public HelpOpCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("helpop").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length > 0) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
                if(profile != null) {
                    if(profile.canUseCommand("/helpop")) {
                        profile.addCommandCooldown("/helpop", 30);
                    } else {
                        player.sendMessage(ChatColor.RED + "This command is currently on cooldown, please wait before trying again.");
                        return true;
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < args.length; i++) {
                sb.append(args[i]);
                if(i + 1 != args.length) {
                    sb.append(" ");
                }
            }

            plugin.getCoreServerManager().sendStaffMessage("&d[Request] &f" + sender.getName() + "&d sent a new request from server &f" + plugin.getCoreServerManager().getCoreServer().getName() + "&d: &f" + sb.toString());
            sender.sendMessage(ChatColor.GREEN + "Your request has been sent to all online staff members.");
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <request>");
        }

        return true;
    }
}
