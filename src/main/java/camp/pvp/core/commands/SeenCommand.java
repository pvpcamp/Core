package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.profiles.MiniProfile;
import camp.pvp.core.server.CoreServer;
import camp.pvp.core.utils.Colors;
import camp.pvp.core.utils.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class SeenCommand implements CommandExecutor {

    private Core plugin;

    public SeenCommand(Core plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("seen");
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

            MiniProfile miniProfile = null;
            for(CoreServer cs : plugin.getCoreServerManager().getCoreServers()) {
                for(MiniProfile mp : cs.getPlayers()) {
                    if(mp.getUuid().equals(profile.getUuid())) {
                        miniProfile = mp;
                        break;
                    }
                }
                if(miniProfile != null) break;
            }

            if(miniProfile != null) {
                sender.sendMessage(Colors.get(profile.getHighestRank().getColor() + profile.getName() + " &6is currently playing on server &f" + miniProfile.getServer() + "&6."));
            } else {
                sender.sendMessage(Colors.get(profile.getHighestRank().getColor() + profile.getName() + " &6was last seen on server &f" + profile.getLastConnectedServer() + " " + DateUtils.getDifference(new Date(), profile.getLastLogout()) + "&6 ago."));
            }
        });

        return true;
    }
}
