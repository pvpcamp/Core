package camp.pvp.core.commands.users;

import camp.pvp.core.SpigotCore;
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
            if(args.length > 0) {
                Player target = Bukkit.getPlayer(args[0]);
                
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            }
        }

        return true;
    }
}
