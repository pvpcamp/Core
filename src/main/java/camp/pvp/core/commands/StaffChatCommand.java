package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffChatCommand implements CommandExecutor {

    private Core plugin;
    public StaffChatCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("staffchat").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
            profile.setStaffChat(!profile.isStaffChat());

            player.sendMessage(ChatColor.GREEN + "Staff chat is now " + ChatColor.WHITE + (profile.isStaffChat() ? ChatColor.YELLOW + "enabled" : ChatColor.RED + "disabled") + ChatColor.GREEN + ".");

        }

        return true;
    }
}
