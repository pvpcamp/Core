package camp.pvp.core.commands.impl;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleGlobalChatCommand implements CommandExecutor {

    private SpigotCore plugin;
    public ToggleGlobalChatCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("toggleglobalchat").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
            profile.setSeeGlobalChat(!profile.isSeeGlobalChat());

            player.sendMessage(ChatColor.GREEN + "Global chat is now " + ChatColor.WHITE + (profile.isSeeGlobalChat() ? ChatColor.YELLOW + "enabled" : ChatColor.RED + "disabled") + ChatColor.GREEN + ".");

        }

        return true;
    }
}
