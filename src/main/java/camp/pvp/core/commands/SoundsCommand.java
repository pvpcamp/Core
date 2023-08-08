package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SoundsCommand implements CommandExecutor {

    private Core plugin;
    public SoundsCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("sounds").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
            profile.setMessageSounds(!profile.isMessageSounds());

            player.sendMessage(ChatColor.GREEN + "Message sounds are now " + ChatColor.WHITE + (profile.isMessageSounds() ? ChatColor.YELLOW + "enabled" : ChatColor.RED + "disabled") + ChatColor.GREEN + ".");

        }

        return true;
    }
}
