package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.guis.ranks.GrantGui;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class GrantsCommand implements CommandExecutor {

    private Core plugin;
    public GrantsCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("grants").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfile(player.getUniqueId());

        if(args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }

        CompletableFuture<CoreProfile> profileFuture = plugin.getCoreProfileManager().findAsync(args[0]);

        profileFuture.thenAccept(target -> {
            if(target == null) {
                player.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                return;
            }

            new GrantGui(plugin, profile, target).open(player);
        });

        return true;
    }
}
