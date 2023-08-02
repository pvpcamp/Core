package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.utils.Colors;
import camp.pvp.core.utils.DateUtils;
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
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AltsCommand {

    @Command(name = "alts", description = "Check the alts of a player.", permission = "core.commands.alts")
    public void alts(CommandArgs args) {

        if (args.length() == 0) {
            args.getSender().sendMessage(ChatColor.RED + "Usage: /" + args.getLabel() + " <player>");
            return;
        }

        String target = args.getArgs(0);
        CoreProfile coreProfile = SpigotCore.getInstance().getCoreProfileManager().find(target, false);

        if (coreProfile == null) {
            args.getSender().sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
            return;
        }

        List<String> ips = coreProfile.getIpList();
        List<String> alts = new ArrayList<>();

        SpigotCore.getInstance().getCoreProfileManager().getMongoManager().getCollection(false, SpigotCore.getInstance().getConfig().getString("networking.mongo.profiles_collection"), new MongoCollectionResult() {
            @Override
            public void call(MongoCollection<Document> mongoCollection) {
                for (String ip : ips) {
                    mongoCollection.find(Filters.eq("ip", ip)).forEach(document -> {
                        String name = document.getString("name");
                        if (name.equalsIgnoreCase(target)) {
                            return;
                        }
                        alts.add(name);
                    });
                }
            }
        });

        int altSize = alts.size() + 1;
        boolean targetOnline = (Bukkit.getPlayer(target) != null && Bukkit.getPlayer(target).isOnline());
        String targetName = coreProfile.getHighestRank().getColor() + coreProfile.getName();
        TextComponent altList = new TextComponent("");
        TextComponent targetHover = new TextComponent("");

        Date targetFirstLogin = coreProfile.getFirstLogin();
        Date targetLastLogin = coreProfile.getLastLogin();
        Date targetLastLogout = coreProfile.getLastLogout();

        if (coreProfile.getActivePunishment(Punishment.Type.BLACKLIST) != null) {
            targetHover.setText(ChatColor.DARK_RED + coreProfile.getName());
        } else if (coreProfile.getActivePunishment(Punishment.Type.BAN) != null) {
            targetHover.setText(ChatColor.RED + coreProfile.getName());
        } else if (coreProfile.getActivePunishment(Punishment.Type.MUTE) != null) {
            targetHover.setText(ChatColor.WHITE.toString() + ChatColor.ITALIC + coreProfile.getName());
        } else if (targetOnline) {
            targetHover.setText(ChatColor.GREEN + coreProfile.getName());
        } else {
            targetHover.setText(ChatColor.GRAY + coreProfile.getName());
        }
        targetHover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "" +
                "&6Name: " + targetName + "\n" +
                (targetOnline ? "&6Online: &f" + DateUtils.getDifference(new Date(), targetLastLogin) : "&6Last Seen: &f" + DateUtils.getDifference(new Date(), targetLastLogout) + " ago") + "\n" +
                "&7&m--------------------------------------" + "\n" +
                "&aCurrently matching " + targetName + "\n" +
                "&7&m--------------------------------------" + "\n" +
                "&6Player Info:" + "\n" +
                "&6First Login: &f" + targetFirstLogin.toString() + "\n" +
                "&6Last Login: &f" + targetLastLogin.toString())).create()));
        targetHover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/c " + coreProfile.getName()));
        altList.addExtra(targetHover);

        args.getSender().sendMessage(Colors.get("&7[&aOnline&7, &7Offline, &f&oMuted&7, &cBan&7, &4Blacklist&7]"));
        args.getSender().sendMessage(Colors.get(targetName + "&6's alts &f(" + altSize + ")&6:"));

        if (altSize == 1) {

            if (args.getSender() instanceof Player) {
                Player player = args.getPlayer();
                player.spigot().sendMessage(altList);
                //sender.sendMessage(Colors.get(name + "&7."));
            } else {
                args.getSender().sendMessage(Colors.get(targetName + "&7."));
            }

        } else {

            StringBuilder g = new StringBuilder();
            g.append(targetName).append("&7, ");
            altList.addExtra(ChatColor.GRAY + ", ");

            alts.forEach(alt -> {

                TextComponent altHover = new TextComponent("");

                CoreProfile altProfile = SpigotCore.getInstance().getCoreProfileManager().find(alt, false);
                String altName = altProfile.getHighestRank().getColor() + altProfile.getName();
                boolean isOnline = (Bukkit.getPlayer(alt) != null && Bukkit.getPlayer(alt).isOnline());
                //boolean isMatching = (coreProfile.getIp().equals(altProfile.getIp()));
                boolean isMatching = false;
                if (coreProfile.getIp().equals(altProfile.getIp())) {
                    isMatching = true;
                }
                Date altFirstLogin = altProfile.getFirstLogin();
                Date altLastLogin = altProfile.getLastLogin();
                Date altLastLogout = altProfile.getLastLogout();

                if (altProfile.getActivePunishment(Punishment.Type.BLACKLIST) != null) {
                    altHover.setText(ChatColor.DARK_RED + altProfile.getName());
                } else if (altProfile.getActivePunishment(Punishment.Type.BAN) != null) {
                    altHover.setText(ChatColor.RED + altProfile.getName());
                } else if (altProfile.getActivePunishment(Punishment.Type.MUTE) != null) {
                    altHover.setText(ChatColor.WHITE.toString() + ChatColor.ITALIC + coreProfile.getName());
                } else if (isOnline) {
                    altHover.setText(ChatColor.GREEN + altProfile.getName());
                } else {
                    altHover.setText(ChatColor.GRAY + altProfile.getName());
                }
                altHover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "" +
                        "&6Name: " + altName + "\n" +
                        (isOnline ? "&6Online: &f" + DateUtils.getDifference(new Date(), altLastLogin) : "&6Last Seen: &f" + DateUtils.getDifference(new Date(), altLastLogout) + " ago") + "\n" +
                        "&7&m--------------------------------------" + "\n" +
                        (isMatching ? "&aCurrently matching " : "&cCurrently not matching ") + targetName + "\n" +
                        "&7&m--------------------------------------" + "\n" +
                        "&6Player Info:" + "\n" +
                        "&6First Login: &f" + altFirstLogin.toString() + "\n" +
                        "&6Last Login: &f" + altLastLogin.toString())).create()));
                altHover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/c " + altProfile.getName()));
                altList.addExtra(altHover);
                if (!alts.get(alts.size() - 1).equals(alt)) {
                    altList.addExtra(ChatColor.GRAY + ", ");
                } else {
                    altList.addExtra(ChatColor.GRAY + ".");
                }
                g.append(altProfile.getHighestRank().getColor()).append(alt);
                if (!alts.get(alts.size() - 1).equals(alt)) {
                    g.append("&7, ");
                } else {
                    g.append("&7.");
                }
            });

            if (args.getSender() instanceof Player) {
                Player player = args.getPlayer();
                player.spigot().sendMessage(altList);
                //sender.sendMessage(Colors.get(g.toString()));
            } else {
                args.getSender().sendMessage(Colors.get(g.toString()));
            }
        }
    }
}