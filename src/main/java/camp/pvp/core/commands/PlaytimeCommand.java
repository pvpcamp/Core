package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import camp.pvp.core.utils.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class PlaytimeCommand implements CommandExecutor {

    private Core plugin;

    public PlaytimeCommand(Core plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("playtime");
        command.setExecutor(this);
        command.setTabCompleter(new PlayerTabCompleter());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }

        CompletableFuture<CoreProfile> profileFuture = plugin.getCoreProfileManager().findAsync(args[0]);

        profileFuture.thenAccept(profile -> {
            if (profile == null) {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                return;
            }

            sender.sendMessage(Colors.get(profile.getHighestRank().getColor() + profile.getName() + "&6's playtime is &f" + DateUtils.getTimeFormat(profile.getCurrentPlaytime()) + "&6."));
        });

        return true;
    }
}
