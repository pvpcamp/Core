package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.Core;
import camp.pvp.core.chattags.ChatTag;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TagCommand {

    private Core plugin;
    public TagCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Command(name = "tag",
            permission = "core.commands.tag",
            description = "Add, remove, or view the list of tags a player owns.")
    public void tagCommand(CommandArgs args) {
        String[] cArgs = args.getArgs();
        CommandSender sender = args.getSender();
        if(cArgs.length > 1) {
            CoreProfile target = plugin.getCoreProfileManager().find(cArgs[1], false);
            if(target != null) {
                switch(cArgs[0].toLowerCase()) {
                    case "list":
                        List<ChatTag> tagList = new ArrayList<>(target.getOwnedChatTags());
                        Collections.sort(tagList);

                        StringBuilder sb = new StringBuilder();
                        sb.append("&6Owned tags for player &f" + target.getName() + " &7(" + tagList.size() + "):&f ");

                        while(!tagList.isEmpty()) {
                            ChatTag tag = tagList.get(0);
                            sb.append("&f" + tag.getName());

                            tagList.remove(tag);

                            if(tagList.isEmpty()) {
                                sb.append(".");
                            } else {
                                sb.append(", ");
                            }
                        }

                        sender.sendMessage(Colors.get(sb.toString()));
                        return;
                    case "clear":
                        target.setChatTag(null);
                        sender.sendMessage(ChatColor.WHITE + target.getName() + ChatColor.GREEN + " no longer has a chat tag applied.");

                        plugin.getCoreProfileManager().exportToDatabase(target, true, false);
                        return;
                    case "add":
                        if(cArgs.length > 2) {
                            ChatTag tag = plugin.getChatTagManager().getTagFromName(cArgs[2]);
                            if(tag == null) {
                                sender.sendMessage(ChatColor.RED + "The tag you specified does not exist.");
                                return;
                            }

                            if(target.getOwnedChatTags().contains(tag)) {
                                sender.sendMessage(ChatColor.RED + target.getName() + " already owns the tag " + tag.getName() + ".");
                                return;
                            }

                            target.getOwnedChatTags().add(tag);
                            plugin.getCoreProfileManager().exportToDatabase(target, true, false);

                            sender.sendMessage(ChatColor.WHITE + target.getName() + ChatColor.GREEN + " now owns the tag " + ChatColor.WHITE + tag.getName() + ChatColor.GREEN + ".");
                            return;
                        }
                        break;
                    case "remove":
                        if(cArgs.length > 2) {
                            ChatTag tag = plugin.getChatTagManager().getTagFromName(cArgs[2]);
                            if(tag == null) {
                                sender.sendMessage(ChatColor.RED + "The tag you specified does not exist.");
                                return;
                            }

                            if(!target.getOwnedChatTags().contains(tag)) {
                                sender.sendMessage(ChatColor.RED + target.getName() + " does not own the tag " + tag.getName() + ".");
                                return;
                            }

                            target.getOwnedChatTags().remove(tag);
                            plugin.getCoreProfileManager().exportToDatabase(target, true, false);

                            sender.sendMessage(ChatColor.WHITE + target.getName() + ChatColor.GREEN + " no longer owns the tag " + ChatColor.WHITE + tag.getName() + ChatColor.GREEN + ".");
                            return;
                        }
                        break;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                return;
            }
        }

        StringBuilder help = new StringBuilder();
        help.append("&6&l/tag &r&6Help");
        help.append("\n&6/tag clear <player> &7- &fClears the currently applied tag from a profile.");
        help.append("\n&6/tag list <player> &7- &fView a list of owned tags.");
        help.append("\n&6/tag add <player> <tag> &7- &fAdd a tag to a profile.");
        help.append("\n&6/tag remove <player> <tag> &7- &fRemove a tag from a profile.");

        sender.sendMessage(Colors.get(help.toString()));
    }
}
