package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class InvalidateCommand implements CommandExecutor {

    private Core plugin;
    public InvalidateCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getCommand("invalidate").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }

        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfile(args[0]);

        if(profile == null) {
            sender.sendMessage(ChatColor.RED + "No loaded profile found for " + args[0] + ".");
            return true;
        }

        if(profile.getPlayer() != null) {
            sender.sendMessage(ChatColor.RED + "This player is currently online, you cannot invalidate their profile.");
            return true;
        }

        profile.setLastLoadFromDatabase(0);
        sender.sendMessage(ChatColor.GREEN + "Successfully invalidated " + profile.getName() + "'s profile.");

        return true;
    }
}
