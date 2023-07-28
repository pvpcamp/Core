package camp.pvp.core.commands;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.utils.Colors;
import camp.pvp.mongo.MongoCollectionResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AltsCommand implements CommandExecutor {

    private SpigotCore plugin;

    public AltsCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("alts").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            return true;
        }

        String target = args[0];
        CoreProfile coreProfile = this.plugin.getCoreProfileManager().find(target, false);

        if (coreProfile == null) {
            sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
            return true;
        }

        String ip = coreProfile.getIp();
        List<String> alts = new ArrayList<>();

        plugin.getCoreProfileManager().getMongoManager().getCollection(false, plugin.getConfig().getString("networking.mongo.profiles_collection"), new MongoCollectionResult() {
            @Override
            public void call(MongoCollection<Document> mongoCollection) {
                mongoCollection.find(Filters.eq("ip", ip)).forEach(document -> {
                    String name = document.getString("name");
                    if (name.equalsIgnoreCase(target)) {
                        return;
                    }
                    alts.add(name);
                });
            }
        });

        if (alts.size() == 0) {

            int altSize = 1;
            boolean isOnline = (Bukkit.getPlayer(target) != null);
            String name = coreProfile.getHighestRank().getColor() + coreProfile.getName();

            // UNCOMMENT ALL WHEN LOGIN TIMES ARE IMPLEMENTED

/*            long firstLogin = document.getLong("firstLogin");
            long lastLogin = document.getLong("firstLogin");
            long lastLogout = document.getLong("firstLogin");
            TextComponent altList = new TextComponent("");
            TextComponent hover = new TextComponent("");

            if (coreProfile.getActivePunishment(Punishment.Type.BLACKLIST) != null) {
                hover.setText(ChatColor.DARK_RED + coreProfile.getName());
            } else if (coreProfile.getActivePunishment(Punishment.Type.BAN) != null) {
                hover.setText(ChatColor.RED + coreProfile.getName());
            } else if (coreProfile.getActivePunishment(Punishment.Type.MUTE) != null) {
                hover.setText(ChatColor.WHITE.toString() + ChatColor.ITALIC + coreProfile.getName());
            } else if (isOnline) {
                hover.setText(ChatColor.GREEN + coreProfile.getName());
            } else {
                hover.setText(ChatColor.GRAY + coreProfile.getName());
            }
            hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "" +
                    "&6Name: " + name + "\n" +
                    (lastLogin > lastLogout ? "&6Online: &f" + formatUtilHere(System.currentTimeMillis() - lastLogin) : "&6Last Seen: &f" + formatTimeHere.getDifference(System.currentTimeMillis(), lastLogout)) + "\n" +
                    "&7&m--------------------------------------" + "\n" +
                    "&aCurrent matching " + name + "\n" +
                    "&7&m--------------------------------------" + "\n" +
                    "&6Current IP Info:" + "\n" +
                    "&6First Login: &f")).create()));
            hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/c " + coreProfile.getName()));
            altList.addExtra(hover);*/

            //sender.sendMessage(Colors.get("&7[&aOnline&7, &7Offline, &f&oMuted&7, &cBan, &4Blacklist&7]"));
            sender.sendMessage(Colors.get(name + "&6's alts &f(1)&6:"));
            if (sender instanceof Player) {
                Player player = (Player) sender;
                //player.spigot().sendMessage(altList);
                sender.sendMessage(Colors.get(name + "&7."));
            } else {
                sender.sendMessage(Colors.get(name + "&7."));
            }
            return true;

        } else {

            int altSize = alts.size() + 1;
            boolean isOnline;
            String name = coreProfile.getHighestRank().getColor() + coreProfile.getName();

            // UNCOMMENT ALL WHEN LOGIN TIMES ARE IMPLEMENTED

/*            long firstLogin = document.getLong("firstLogin");
            long lastLogin = document.getLong("firstLogin");
            long lastLogout = document.getLong("firstLogin");
            TextComponent altList = new TextComponent("");
            TextComponent hover = new TextComponent("");

            alts.forEach(alt -> {
                CoreProfile altProfile = plugin.getCoreProfileManager().find(alt, false);
                isOnline = (Bukkit.getPlayer(alt) != null)

                if (coreProfile.getActivePunishment(Punishment.Type.BLACKLIST) != null) {
                    hover.setText(ChatColor.DARK_RED + coreProfile.getName());
                } else if (coreProfile.getActivePunishment(Punishment.Type.BAN) != null) {
                    hover.setText(ChatColor.RED + coreProfile.getName());
                } else if (coreProfile.getActivePunishment(Punishment.Type.MUTE) != null) {
                    hover.setText(ChatColor.WHITE.toString() + ChatColor.ITALIC + coreProfile.getName());
                } else if (isOnline) {
                    hover.setText(ChatColor.GREEN + coreProfile.getName());
                } else {
                    hover.setText(ChatColor.GRAY + coreProfile.getName());
                }
                hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "" +
                        "&6Name: " + name + "\n" +
                        (lastLogin > lastLogout ? "&6Online: &f" + formatUtilHere(System.currentTimeMillis() - lastLogin) : "&6Last Seen: &f" + formatTimeHere.getDifference(System.currentTimeMillis(), lastLogout)) + "\n" +
                        "&7&m--------------------------------------" + "\n" +
                        "&aCurrent matching " + name + "\n" +
                        "&7&m--------------------------------------" + "\n" +
                        "&6Current IP Info:" + "\n" +
                        "&6First Login: &f")).create()));
                hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/c " + coreProfile.getName()));
                altList.addExtra(hover);
                if (alts.get(alts.size() - 1) != alt) {
                    altList.addExtra(ChatColor.GRAY + ", ");
                } else {
                    altList.addExtra(ChatColor.GRAY + ".");
                }
            });*/

            StringBuilder g = new StringBuilder();
            g.append(coreProfile.getHighestRank().getColor() + coreProfile.getName() + "&7, ");

            alts.forEach(alt -> {
                CoreProfile altProfile = plugin.getCoreProfileManager().find(alt, false);
                g.append(altProfile.getHighestRank().getColor() + alt);
                if (alts.get(alts.size() - 1).equals(alt)) {
                    g.append("&7.");
                } else {
                    g.append("&7, ");
                }
            });

            //sender.sendMessage(Colors.get("&7[&aOnline&7, &7Offline, &f&oMuted&7, &cBan, &4Blacklist&7]"));
            sender.sendMessage(Colors.get(name + "&6's alts &f(" + alts.size() + ")&6:"));
            if (sender instanceof Player) {
                Player player = (Player) sender;
                //player.spigot().sendMessage(altList);
                sender.sendMessage(Colors.get(g.toString()));
            } else {
                sender.sendMessage(Colors.get(g.toString()));
            }
            return true;
        }
    }
}