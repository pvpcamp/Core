package camp.pvp.core.commands.tags;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.chattags.ChatTag;
import camp.pvp.core.chattags.ChatTagManager;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TagManagerCommand implements CommandExecutor {

    private SpigotCore plugin;
    public TagManagerCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("tagmanager").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
            ChatTag tag = null;
            ChatTagManager tagManager = plugin.getChatTagManager();

            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "list":
                        List<ChatTag> tagList = new ArrayList<>(tagManager.getChatTags().values());
                        Collections.sort(tagList);

                        StringBuilder sb = new StringBuilder();
                        sb.append("&6Tags &7(" + tagList.size() + "):&f ");

                        while (!tagList.isEmpty()) {
                            tag = tagList.get(0);
                            sb.append("&f" + tag.getName());

                            tagList.remove(tag);

                            if (tagList.isEmpty()) {
                                sb.append(".");
                            } else {
                                sb.append(", ");
                            }
                        }

                        player.sendMessage(Colors.get(sb.toString()));
                        return true;
                    case "make":
                    case "create":
                        if (args.length > 1) {
                            tag = tagManager.getTagFromName(args[1]);
                            if (tag == null) {
                                if (!args[1].matches("[a-zA-Z]+")) {
                                    player.sendMessage(ChatColor.RED + "Tag names can only contain letters A-Z.");
                                    return true;
                                }

                                tag = tagManager.create(args[1]);
                                player.sendMessage(Colors.get("&aTag &f" + tag.getDisplayName() + "&a has been created."));
                            } else {
                                player.sendMessage(ChatColor.RED + "This tag already exists.");
                            }

                            return true;
                        }
                        break;
                    case "delete":
                        if (args.length > 1) {
                            tag = tagManager.getTagFromName(args[1]);
                            if (tag != null) {
                                tagManager.delete(tag);
                                player.sendMessage(Colors.get("&aTag &f" + tag.getDisplayName() + "&a has been deleted."));
                            } else {
                                player.sendMessage(ChatColor.RED + "The tag you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "rename":
                        if (args.length > 2) {
                            tag = tagManager.getTagFromName(args[1]);
                            if (tag != null) {
                                String name = args[2];
                                if (!name.matches("[a-zA-Z]+")) {
                                    player.sendMessage(ChatColor.RED + "Tag names can only contain letters A-Z.");
                                    return true;
                                }

                                player.sendMessage(Colors.get("&aTag &f" + tag.getName() + "&a has been renamed to &f" + name.toLowerCase() + "&a."));
                                tag.setName(name.toLowerCase());
                                tagManager.exportToDatabase(tag, true);
                            } else {
                                player.sendMessage(ChatColor.RED + "The tag you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "displayname":
                        if (args.length > 2) {
                            tag = tagManager.getTagFromName(args[1]);
                            if (tag != null) {
                                sb = new StringBuilder();
                                for (int i = 2; i < args.length; i++) {
                                    sb.append(args[i]);
                                    if (i + 1 != args.length) {
                                        sb.append(" ");
                                    }
                                }

                                tag.setDisplayName(sb.toString());
                                tagManager.exportToDatabase(tag, true);

                                player.sendMessage(Colors.get("&aTag &f" + tag.getName() + "&a now has the display name &f" + tag.getDisplayName() + "&a."));
                            } else {
                                player.sendMessage(ChatColor.RED + "The tag you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "settag":
                        if (args.length > 2) {
                            tag = tagManager.getTagFromName(args[1]);
                            if (tag != null) {
                                sb = new StringBuilder();
                                for (int i = 2; i < args.length; i++) {
                                    sb.append(args[i]);
                                    if (i + 1 != args.length) {
                                        sb.append(" ");
                                    }
                                }

                                tag.setTag(sb.toString());
                                tagManager.exportToDatabase(tag, true);

                                player.sendMessage(Colors.get("&aTag &f" + tag.getName() + "&a now has the suffix &f" + tag.getTag() + "&a."));
                            } else {
                                player.sendMessage(ChatColor.RED + "The tag you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "visible":
                        if (args.length > 2) {
                            tag = tagManager.getTagFromName(args[1]);
                            if (tag != null) {
                                boolean b = Boolean.parseBoolean(args[2]);

                                tag.setVisible(b);
                                tagManager.exportToDatabase(tag, true);

                                player.sendMessage(Colors.get("&aTag &f" + tag.getName() + "&a visibility has been set to &f" + tag.isVisible() + "&a."));
                            } else {
                                player.sendMessage(ChatColor.RED + "The tag you specified does not exist.");
                            }
                            return true;
                        }
                        break;

                }
            }

            StringBuilder help = new StringBuilder();
            help.append("&6&l/tagmanager &r&6Help");
            help.append("\n&7<> Required, [] Optional");
            help.append("\n&6/tagmanager list &7- &fReturns the list of ranks in weight order.");
            help.append("\n&6/tagmanager create <name> &7- &fCreates a new tag.");
            help.append("\n&6/tagmanager delete <name> &7- &fDeletes an existing tag.");
            help.append("\n&6/tagmanager rename <name> <new name> &7- &fRenames a tag.");
            help.append("\n&6/tagmanager displayname <name> <display name> &7- &fSets a tag display name.");
            help.append("\n&6/tagmanager settag <name> <tag> &7- &fSets a tag suffix.");
            help.append("\n&6/tagmanager visible <name> <boolean> &7- &fShow in the tags GUI, even if a player does not own it.");

            player.sendMessage(Colors.get(help.toString()));
        }

        return true;
    }
}
