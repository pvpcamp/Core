package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.chattags.ChatTag;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import camp.pvp.practice.arenas.Arena;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TagCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUB_COMMANDS = Arrays.asList("list", "add", "remove", "clear");

    private Core plugin;
    public TagCommand(Core plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("tag");
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        final List<String> completions = new ArrayList<>();

        if(args.length == 1) {
            StringUtil.copyPartialMatches(args[0], SUB_COMMANDS, completions);
            return completions;
        }

        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length < 2) {
            sender.sendMessage(help());
            return true;
        }

        CompletableFuture<CoreProfile> profileFuture = plugin.getCoreProfileManager().findAsync(args[1]);
        profileFuture.thenAccept(profile -> {
            if(profile == null) {
                sender.sendMessage(ChatColor.RED + "The target you specified does not have a profile on the network.");
                return;
            }

            switch(args[0].toLowerCase()) {
                case "list":
                    list(sender, profile);
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
                case "clear":
                    clear(sender, profile);
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
        help.append("&6&l/tag &r&6Help");
        help.append("\n&6/tag clear <player> &7- &fClears the currently applied tag from a profile.");
        help.append("\n&6/tag list <player> &7- &fView a list of owned tags.");
        help.append("\n&6/tag add <player> <tag> &7- &fAdd a tag to a profile.");
        help.append("\n&6/tag remove <player> <tag> &7- &fRemove a tag from a profile.");

        return Colors.get(help.toString());
    }

    private void list(CommandSender sender, CoreProfile profile) {
        List<ChatTag> tagList = new ArrayList<>(profile.getOwnedChatTags());
        Collections.sort(tagList);

        StringBuilder sb = new StringBuilder();
        sb.append("&6Owned tags for player &f" + profile.getName() + " &7(" + tagList.size() + "):&f ");

        while (!tagList.isEmpty()) {
            ChatTag tag = tagList.get(0);
            sb.append("&f" + tag.getName());

            tagList.remove(tag);

            if (tagList.isEmpty()) {
                sb.append(".");
            } else {
                sb.append(", ");
            }
        }

        sender.sendMessage(Colors.get(sb.toString()));
    }

    private void add(CommandSender sender, CoreProfile profile, String tag) {
        ChatTag chatTag = plugin.getChatTagManager().getTagFromName(tag);
        if(chatTag == null) {
            sender.sendMessage(ChatColor.RED + "The tag you specified does not exist.");
            return;
        }

        if(profile.getOwnedChatTags().contains(chatTag)) {
            sender.sendMessage(ChatColor.RED + profile.getName() + " already owns the tag " + chatTag.getName() + ".");
            return;
        }

        profile.getOwnedChatTags().add(chatTag);

        if(profile.getPlayer() == null && profile.getPlayer().isOnline()) {
            plugin.getCoreProfileManager().exportToDatabase(profile, true);
        }

        sender.sendMessage(ChatColor.WHITE + profile.getName() + ChatColor.GREEN + " now owns the tag " + ChatColor.WHITE + chatTag.getName() + ChatColor.GREEN + ".");
    }

    private void remove(CommandSender sender, CoreProfile profile, String tag) {
        ChatTag chatTag = plugin.getChatTagManager().getTagFromName(tag);
        if(chatTag == null) {
            sender.sendMessage(ChatColor.RED + "The tag you specified does not exist.");
            return;
        }

        if(!profile.getOwnedChatTags().contains(chatTag)) {
            sender.sendMessage(ChatColor.RED + profile.getName() + " does not own the tag " + chatTag.getName() + ".");
            return;
        }

        profile.getOwnedChatTags().remove(chatTag);

        if(profile.getPlayer() == null && profile.getPlayer().isOnline()) {
            plugin.getCoreProfileManager().exportToDatabase(profile, true);
        }

        sender.sendMessage(ChatColor.WHITE + profile.getName() + ChatColor.GREEN + " no longer owns the tag " + ChatColor.WHITE + chatTag.getName() + ChatColor.GREEN + ".");
    }

    private void clear(CommandSender sender, CoreProfile profile) {
        profile.setChatTag(null);
        sender.sendMessage(ChatColor.WHITE + profile.getName() + ChatColor.GREEN + " no longer has a chat tag applied.");

        if(profile.getPlayer() == null && profile.getPlayer().isOnline()) {
            plugin.getCoreProfileManager().exportToDatabase(profile, true);
        }
    }
}
