package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.ChatHistory;
import camp.pvp.core.utils.Colors;
import camp.pvp.core.utils.PaginatedMessage;
import camp.pvp.mongo.MongoCollectionResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.*;

public class UserHistoryCommand implements CommandExecutor {

    private Core plugin;
    public UserHistoryCommand(Core plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("userhistory");
        command.setExecutor(this);
        command.setTabCompleter(new PlayerTabCompleter());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length > 0) {
            String name = args[0];
            if(!name.matches("^[a-zA-Z0-9_]{1,16}$")) {
                sender.sendMessage(ChatColor.RED + "Invalid username provided.");
                return true;
            }

            ChatHistory.Type type = null;
            int page = 1;

            if(args.length > 1) {
                type = ChatHistory.Type.fromString(args[1]);
            }

            if(args.length > 2) {
                try {
                    page = Integer.parseInt(args[2]);
                } catch (NumberFormatException ignored) {
                    sender.sendMessage(ChatColor.RED + "Invalid page number.");
                    return true;
                }
            }

            if (page < 1) {
                sender.sendMessage(ChatColor.RED + "Invalid page number.");
                return true;
            }

            PaginatedMessage message = new PaginatedMessage("&6History for &f" + name + "&6 for type &f" + (type == null ? "any" : type.name().toLowerCase()) + "&6. &7(<page>/<pages>)", 10);
            final int fPage = page;
            final ChatHistory.Type fType = type;
            plugin.getCoreProfileManager().getMongoManager().getCollection(true, plugin.getConfig().getString("networking.mongo.chat_history_collection"), new MongoCollectionResult() {
                @Override
                public void call(MongoCollection<Document> mongoCollection) {
                    List<ChatHistory> history = new ArrayList<>();
                    mongoCollection.find(Filters.regex("player_name", "(?i)" + name)).forEach(
                            document -> {
                                String dbName = document.getString("player_name");
                                if(dbName.equalsIgnoreCase(name)) {
                                    ChatHistory chatHistory = new ChatHistory(document);
                                    history.add(chatHistory);
                                }
                            }
                    );

                    if(history.isEmpty()) {
                        sender.sendMessage(Colors.get("&cNo message history found for " + name + "."));
                    } else {
                        Collections.sort(history, new Comparator<ChatHistory>() {
                            @Override
                            public int compare(ChatHistory o1, ChatHistory o2) {
                                return o1.compareTo(o2);
                            }
                        });

                        for (ChatHistory ch : history) {
                            if(fType != null) {
                                if(ch.getType().equals(fType)) {
                                    message.getEntries().add(ch.getMessage());
                                }
                            } else {
                                message.getEntries().add(ch.getMessage());
                            }
                        }

                        sender.sendMessage(message.getPage(fPage));

                    }
                }
            });
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <user> [type] [page]");
        }

        return true;
    }
}
