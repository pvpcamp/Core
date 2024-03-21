package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.server.CoreServerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ReportCommand implements CommandExecutor {

    private Core plugin;
    public ReportCommand(Core plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("report");
        command.setExecutor(this);
        command.setTabCompleter(new PlayerTabCompleter());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
            if(!profile.canUseCommand("/report")) {
                player.sendMessage(ChatColor.RED + "This command is currently on cooldown, please wait before trying again.");
                return true;
            }

            if(args.length > 0) {
                Player target = Bukkit.getPlayer(args[0]);

                if(target == null) {
                    player.sendMessage(ChatColor.RED + "The player you specified is not on this server.");
                    return true;
                }

                if(player == target) {
                    player.sendMessage(ChatColor.RED + "You cannot report yourself.");
                    return true;
                }

                StringBuilder reason = new StringBuilder();
                for(int i = 1; i < args.length; i++) {
                    reason.append(args[i]);

                    if(i != args.length - 1) {
                        reason.append(" ");
                    }
                }

                final CoreServerManager csm = plugin.getCoreServerManager();
                csm.sendStaffMessage("&c[Report] &f" + profile.getName() + " &chas reported &f" + target.getName() + "&c on server &f" + csm.getCoreServer().getName() + "&c for: &f" + reason.toString());
                profile.addCommandCooldown("/report", 30);
                player.sendMessage(ChatColor.GREEN + "Your report has been submitted to all online staff on the network.");
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            }
        }

        return true;
    }
}
