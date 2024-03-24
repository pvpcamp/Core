package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.utils.Colors;
import com.mongodb.client.model.Filters;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
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

        if(!(sender instanceof Player player)) return true;

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

            TextComponent component = new TextComponent(Colors.get("&6Alts of " + profile.getName() + "&7(" + alts.size() + "): &f"));

            int x = 0;
            for(CoreProfile alt : alts) {
                TextComponent altComponent = new TextComponent();

                Punishment punishment = null;
                for(Punishment.Type type : Punishment.Type.values()) {
                    Punishment p = alt.getActivePunishment(type);
                    if(p != null) {
                        punishment = p;
                    }
                }

                String color = alt.getPlayer() != null && alt.getPlayer().isOnline() ? "&a&l" : "&f";

                if(punishment != null) {
                    switch(punishment.getType()) {
                        case BLACKLIST:
                            color = "&4";
                            break;
                        case BAN:
                            color = "&c";
                            break;
                    }
                }

                altComponent.setText(Colors.get(color + alt.getName()));

                altComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/history " + alt.getName()));

                HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Colors.get(
                        "&6Name: &f" + alt.getName() + "\n" +
                        "&6Rank: &f" + alt.getHighestRank().getDisplayName() + "\n" +
                        "&6First Login: &f" + alt.getFirstLogin() + "\n" +
                        "&6Last Login: &f" + alt.getLastLogin() + "\n" +
                        "&6Punishments: &f" + alt.getPunishments().size() + "\n" +
                        "&6Last Online: &f" + alt.getLastLogin() + "\n" +
                        "&6Last Connected Server: &f" + alt.getLastConnectedServer() + "\n" +
                        (profile.getIp().contains(alt.getIp()) ? "&cMatching " + profile.getName() + "'s IP." : "&7Not matching " + profile.getName() + "'s IP.")
                )).create());

                altComponent.setHoverEvent(hover);

                x++;
                if(x == alts.size()) {
                    altComponent.addExtra(Colors.get("&7."));
                } else {
                    altComponent.addExtra(Colors.get("&7, "));
                }

                component.addExtra(altComponent);
            }

            player.spigot().sendMessage(component);
        });

        return true;
    }
}