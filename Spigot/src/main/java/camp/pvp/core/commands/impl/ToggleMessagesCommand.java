package camp.pvp.core.commands.impl;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleMessagesCommand implements CommandExecutor {

    private SpigotCore plugin;
    public ToggleMessagesCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("togglemessages").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
            profile.setAllowPrivateMessages(!profile.isAllowPrivateMessages());

            player.sendMessage(ChatColor.GREEN + "Private messages are now " + ChatColor.WHITE + (profile.isAllowPrivateMessages() ? ChatColor.YELLOW + "enabled" : ChatColor.RED + "disabled") + ChatColor.GREEN + ".");

        }

        return true;
    }
}
