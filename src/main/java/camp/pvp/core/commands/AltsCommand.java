package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.utils.Colors;
import com.mongodb.client.model.Filters;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AltsCommand implements CommandExecutor{
    
    private Core plugin;
    
    public AltsCommand(Core plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("alts");
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
        profileFuture.thenAcceptAsync(profile -> {
            if(profile == null) {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
            }

            List<String> ips = profile.getIpList();
            List<CoreProfile> alts = new ArrayList<>();

            plugin.getCoreProfileManager()
                    .getMongoManager()
                    .getDatabase()
                    .getCollection(plugin.getCoreProfileManager().getProfilesCollectionName())
                    .find(Filters.in("ip", ips)).forEach(document -> {
                CoreProfile alt = new CoreProfile(document.get("_id", UUID.class));
                alt.importFromDocument(plugin, document);
                alt.setLastLoadFromDatabase(System.currentTimeMillis());
                plugin.getCoreProfileManager().getLoadedProfiles().put(alt.getUuid(), alt);
                alts.add(alt);
            });

            StringBuilder sb = new StringBuilder();
            sb.append("&6Alts of ").append(profile.getName()).append("&7: ");

            int x = 0;
            for(CoreProfile alt : alts) {

                Punishment punishment = null;
                for(Punishment.Type type : Punishment.Type.values()) {
                    Punishment p = alt.getActivePunishment(type);
                    if(p != null) {
                        punishment = p;
                    }
                }

                sb.append("&f").append(alt.getName());

                if(punishment != null) {
                    switch(punishment.getType()) {
                        case BLACKLIST:
                            sb.append(ChatColor.DARK_RED).append(" BLACKLISTED");
                            break;
                        case BAN:
                            sb.append(ChatColor.RED).append(" BANNED");
                            break;
                    }
                }

                x++;
                if(x == alts.size()) {
                    sb.append("&7.");
                } else {
                    sb.append("&7, ");
                }
            }

            sender.sendMessage(Colors.get(sb.toString()));
        });

        return true;
    }
}