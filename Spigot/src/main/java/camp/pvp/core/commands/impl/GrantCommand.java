package camp.pvp.core.commands.impl;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.guis.ranks.GrantGui;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.profiles.Grant;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.utils.Colors;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

public class GrantCommand implements CommandExecutor {

    private SpigotCore plugin;
    public GrantCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("grant").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length > 1) {
            String name = args[1];
            CoreProfile profile = plugin.getCoreProfileManager().find(name, false);
            Rank rank = null;

            if (profile != null) {
                switch(args[0].toLowerCase()) {
                    case "list":
                        List<Rank> rankList = new ArrayList<>(profile.getRanks());
                        Collections.sort(rankList);

                        StringBuilder sb = new StringBuilder();
                        sb.append("&6Grants for player &f" + profile.getName() + " &7(" + rankList.size() + "):&f ");

                        while(!rankList.isEmpty()) {
                            rank = rankList.get(0);
                            sb.append(rank.getColor() + rank.getName());

                            rankList.remove(rank);

                            if(rankList.isEmpty()) {
                                sb.append(".");
                            } else {
                                sb.append(", ");
                            }
                        }

                        sender.sendMessage(Colors.get(sb.toString()));
                        return true;
                    case "clear":
                        profile.getRanks().clear();
                        profile.getRanks().add(plugin.getRankManager().getDefaultRank());
                        plugin.getCoreProfileManager().updatePermissions(profile);
                        plugin.getCoreProfileManager().exportToDatabase(profile, true, false);

                        sender.sendMessage(Colors.get("&aGrants have been cleared for player &f" + profile.getName() + "&a."));
                        return true;
                    case "add":
                        if(args.length > 2) {
                            rank = plugin.getRankManager().getRankFromName(args[2]);
                            if(rank != null) {
                                if(!profile.getRanks().contains(rank)) {
                                    profile.getRanks().add(rank);
                                    plugin.getCoreProfileManager().updatePermissions(profile);
                                    plugin.getCoreProfileManager().exportToDatabase(profile, true, false);

                                    sender.sendMessage(Colors.get("&aPlayer " + profile.getName() + " now has the rank " + rank.getColor() + rank.getName() + "&a."));
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Player " + profile.getName() + " already has the rank " + rank.getName() + ".");
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "remove":
                        if(args.length > 2) {
                            rank = plugin.getRankManager().getRankFromName(args[2]);
                            if(rank != null) {
                                if(profile.getRanks().contains(rank)) {
                                    profile.getRanks().remove(rank);
                                    plugin.getCoreProfileManager().updatePermissions(profile);
                                    plugin.getCoreProfileManager().exportToDatabase(profile, true, false);
                                    sender.sendMessage(Colors.get("&aPlayer " + profile.getName() + " no longer has the rank " + rank.getColor() + rank.getName() + "&a."));
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Player " + profile.getName() + " does not have the rank " + rank.getName() + ".");
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                return true;
            }
        }

        StringBuilder help = new StringBuilder();
        help.append("&6&l/grant &r&6Help");
        help.append("\n&6/grant list <player> &7- &fView the list of grants a player has.");
        help.append("\n&6/grant clear <player> &7- &fClears a player's grants.");
        help.append("\n&6/grant add <player> <rank> &7- &fGrant a player a rank.");
        help.append("\n&6/grant remove <player> <rank> &7- &fRemove a rank from a player.");

        sender.sendMessage(Colors.get(help.toString()));

        return true;
    }
}
