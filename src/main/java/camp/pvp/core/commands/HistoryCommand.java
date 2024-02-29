package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.guis.punishments.HistoryGui;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class HistoryCommand implements CommandExecutor {

    private Core plugin;
    public HistoryCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("history").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)) return true;

        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }

        CoreProfile opener = plugin.getCoreProfileManager().getLoadedProfile(player.getUniqueId());

        CompletableFuture<CoreProfile> profileFuture = plugin.getCoreProfileManager().findAsync(args[0]);
        profileFuture.thenAccept(profile -> {
            if(profile == null) {
                player.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                return;
            }

            if(profile.getPunishments().isEmpty()) {
                player.sendMessage(ChatColor.GREEN + "Player " + ChatColor.WHITE +  profile.getName() + ChatColor.GREEN + " does not have any punishments.");
                return;
            }

            new HistoryGui(profile, profile.getPunishments(), false, opener).open(player);
        });

        return true;
    }
}
