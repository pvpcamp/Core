package camp.pvp.core.commands.staff;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffModeCommand implements CommandExecutor {

    private SpigotCore plugin;
    public StaffModeCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("staffmode").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
            profile.setStaffMode(!profile.isStaffMode());

            player.sendMessage(ChatColor.GREEN + "Staff mode is now " + ChatColor.WHITE + (profile.isStaffMode() ? ChatColor.YELLOW + "enabled" : ChatColor.RED + "disabled") + ChatColor.GREEN + ".");

        }

        return true;
    }
}