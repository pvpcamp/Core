package camp.pvp.core.commands.users;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.guis.reports.ReportGui;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportCommand implements CommandExecutor {

    private SpigotCore plugin;
    public ReportCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("report").setExecutor(this);
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
                if(target != null && player != target) {
                    new ReportGui(profile, target.getName()).open(player);
                } else {
                    player.sendMessage(ChatColor.RED + "The player that you specified is not on this server.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            }
        }

        return true;
    }
}
