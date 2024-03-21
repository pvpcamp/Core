package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class GrantCommand implements CommandExecutor {

    private Core plugin;
    public GrantCommand(Core plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("grant");
        command.setExecutor(this);
        command.setTabCompleter(new PlayerTabCompleter());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length < 2) {
            sender.sendMessage(help());
            return true;
        }

        CompletableFuture<CoreProfile> profileFuture = plugin.getCoreProfileManager().findAsync(args[1]);
        profileFuture.thenAccept(profile -> {
            if (profile == null) {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                return;
            }

            switch(args[0].toLowerCase()) {
                case "list":
                    list(sender, profile);
                    break;
                case "clear":
                    clear(sender, profile);
                    break;
                case "add":
                    if(args.length < 3) {
                        sender.sendMessage(help());
                    } else {
                        add(sender, profile, args[2]);
                    }
                    break;
                case "remove":
                    if(args.length < 3) {
                        sender.sendMessage(help());
                    } else {
                        remove(sender, profile, args[2]);
                    }
                    break;
                default:
                    sender.sendMessage(help());
                    break;
            }
        });

        return true;
    }

    private String help() {
        StringBuilder help = new StringBuilder();
        help.append("&6&l/grant &r&6Help");
        help.append("\n&6/grant list <player> &7- &fView the list of grants a player has.");
        help.append("\n&6/grant clear <player> &7- &fClears a player's grants.");
        help.append("\n&6/grant add <player> <rank> &7- &fGrant a player a rank.");
        help.append("\n&6/grant remove <player> <rank> &7- &fRemove a rank from a player.");

        return Colors.get(help.toString());
    }

    private void list(CommandSender sender, CoreProfile profile) {
        List<Rank> rankList = new ArrayList<>(profile.getRanks());
        Collections.sort(rankList);

        StringBuilder sb = new StringBuilder();
        sb.append("&6Grants for player &f" + profile.getName() + " &7(" + rankList.size() + "):&f ");

        while(!rankList.isEmpty()) {
            Rank rank = rankList.get(0);
            sb.append(rank.getColor() + rank.getName());

            rankList.remove(rank);

            if(rankList.isEmpty()) {
                sb.append(".");
            } else {
                sb.append(", ");
            }
        }

        sender.sendMessage(Colors.get(sb.toString()));
    }

    private void clear(CommandSender sender, CoreProfile profile) {
        profile.getRanks().clear();
        profile.getRanks().add(plugin.getRankManager().getDefaultRank());
        plugin.getCoreProfileManager().updatePermissions(profile);
        plugin.getCoreProfileManager().exportToDatabase(profile, true);

        sender.sendMessage(Colors.get("&aGrants have been cleared for player &f" + profile.getName() + "&a."));
    }

    private void add(CommandSender sender, CoreProfile profile, String rankName) {
        Rank rank = plugin.getRankManager().getRankFromName(rankName);

        if(rank == null) {
            sender.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
            return;
        }

        if(profile.getRanks().contains(rank)) {
            sender.sendMessage(ChatColor.RED + "Player " + profile.getName() + " already has the rank " + rank.getName() + ".");
            return;
        }

        profile.getRanks().add(rank);
        plugin.getCoreProfileManager().updatePermissions(profile);
        plugin.getCoreProfileManager().exportToDatabase(profile, true);

        sender.sendMessage(Colors.get("&aPlayer " + profile.getName() + " now has the rank " + rank.getColor() + rank.getName() + "&a."));
    }

    private void remove(CommandSender sender, CoreProfile profile, String rankName) {
        Rank rank = plugin.getRankManager().getRankFromName(rankName);

        if(rank == null) {
            sender.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
            return;
        }

        if(!profile.getRanks().contains(rank)) {
            sender.sendMessage(ChatColor.RED + "Player " + profile.getName() + " does not have the rank " + rank.getName() + ".");
            return;
        }

        profile.getRanks().remove(rank);
        plugin.getCoreProfileManager().updatePermissions(profile);
        plugin.getCoreProfileManager().exportToDatabase(profile, true);

        sender.sendMessage(Colors.get("&aPlayer " + profile.getName() + " no longer has the rank " + rank.getColor() + rank.getName() + "&a."));
    }
}
