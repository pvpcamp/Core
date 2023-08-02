package camp.pvp.core.commands;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.ranks.RankManager;
import camp.pvp.core.utils.Colors;
import camp.pvp.events.MongoMessageEvent;
import camp.pvp.mongo.MongoCollectionResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class RankCommand implements CommandExecutor {

    private SpigotCore plugin;
    public RankCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("rank").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
            Rank rank = null;
            RankManager rankManager = plugin.getRankManager();

            if(args.length > 0) {
                switch(args[0].toLowerCase()) {
                    case "list":
                        List<Rank> rankList = new ArrayList<>(plugin.getRankManager().getRanks().values());
                        Collections.sort(rankList);

                        StringBuilder sb = new StringBuilder();
                        sb.append("&6Ranks &7(" + rankList.size() + "):&f ");

                        while(!rankList.isEmpty()) {
                            rank = rankList.get(0);
                            sb.append(rank.getColor() + rank.getName() + " &7(" + rank.getWeight() + ")");

                            rankList.remove(rank);

                            if(rankList.isEmpty()) {
                                sb.append(".");
                            } else {
                                sb.append(", ");
                            }
                        }

                        player.sendMessage(Colors.get(sb.toString()));
                        return true;
                    case "make":
                    case "create":
                        if(args.length > 2) {
                            rank = rankManager.getRankFromName(args[1]);
                            if(rank == null) {
                                if(!args[1].matches("[a-zA-Z]+")) {
                                    player.sendMessage(ChatColor.RED + "Rank names can only contain letters A-Z.");
                                    return true;
                                }

                                int weight = 0;
                                try {
                                    weight = Integer.parseInt(args[2]);
                                } catch (NumberFormatException ignored) {
                                    player.sendMessage(ChatColor.RED + "You must specify a valid weight number.");
                                    return true;
                                }

                                if(rankManager.getRankFromWeight(weight) == null) {
                                    rank = rankManager.create(args[1]);
                                    rank.setWeight(weight);

                                    player.sendMessage(Colors.get("&aRank " + rank.getColor() + rank.getDisplayName() + "&a with a weight of " + weight + " has been created."));
                                }

                            } else {
                                player.sendMessage(ChatColor.RED + "This rank already exists.");
                            }

                            return true;
                        }
                        break;
                    case "delete":
                        if(args.length > 1) {
                            rank = rankManager.getRankFromName(args[1]);
                            if(rank != null) {
                                if(rank.isDefaultRank()) {
                                    player.sendMessage(ChatColor.RED + "You cannot delete the default rank.");
                                    return true;
                                }

                                rankManager.delete(rank);
                                player.sendMessage(Colors.get("&aRank " + rank.getColor() + rank.getDisplayName() + "&a has been deleted."));
                            } else {
                                player.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "rename":
                        if(args.length > 2) {
                            rank = rankManager.getRankFromName(args[1]);
                            if(rank != null) {
                                String name = args[2];
                                if (!name.matches("[a-zA-Z]+")) {
                                    player.sendMessage(ChatColor.RED + "Rank names can only contain letters A-Z.");
                                    return true;
                                }

                                player.sendMessage(Colors.get("&aRank " + rank.getColor() + rank.getName() + "&a has been renamed to " + rank.getColor() + name.toLowerCase() + "&a."));
                                rank.setName(name.toLowerCase());
                                rankManager.exportToDatabase(rank, true);
                            } else {
                                player.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "weight":
                        if(args.length > 2) {
                            rank = rankManager.getRankFromName(args[1]);
                            if(rank != null) {
                                int weight = 0;
                                try {
                                    weight = Integer.parseInt(args[2]);
                                } catch (NumberFormatException ignored) {
                                    player.sendMessage(ChatColor.RED + "You must specify a valid weight number.");
                                    return true;
                                }

                                rank.setWeight(weight);
                                rankManager.exportToDatabase(rank, true);

                                player.sendMessage(Colors.get("&aRank " + rank.getColor() + rank.getName() + "&a now has the weight " + rank.getWeight() + "."));
                            } else {
                                player.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "displayname":
                        if(args.length > 2) {
                            rank = rankManager.getRankFromName(args[1]);
                            if(rank != null) {
                                sb = new StringBuilder();
                                for(int i = 2; i < args.length; i++) {
                                    sb.append(args[i]);
                                    if(i + 1 != args.length) {
                                        sb.append(" ");
                                    }
                                }

                                rank.setDisplayName(sb.toString());
                                rankManager.exportToDatabase(rank, true);

                                player.sendMessage(Colors.get("&aRank " + rank.getColor() + rank.getName() + "&a now has the display name &f" + rank.getDisplayName() + "&a."));
                            } else {
                                player.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "prefix":
                        if(args.length > 2) {
                            rank = rankManager.getRankFromName(args[1]);
                            if(rank != null) {
                                sb = new StringBuilder();
                                for(int i = 2; i < args.length; i++) {
                                    sb.append(args[i]);
                                    if(i + 1 != args.length) {
                                        sb.append(" ");
                                    }
                                }

                                if(sb.toString().equalsIgnoreCase("none")) {
                                    rank.setPrefix(null);
                                    rankManager.exportToDatabase(rank, true);

                                    player.sendMessage(Colors.get("&aRank " + rank.getColor() + rank.getName() + "&a no longer has a prefix."));
                                } else {
                                    rank.setPrefix(sb.toString());
                                    rankManager.exportToDatabase(rank, true);

                                    player.sendMessage(Colors.get("&aRank " + rank.getColor() + rank.getName() + "&a now has the prefix &f" + rank.getPrefix() + "&a."));
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "color":
                        if(args.length > 2) {
                            rank = rankManager.getRankFromName(args[1]);
                            if(rank != null) {
                                player.sendMessage(Colors.get("&aRank " + rank.getColor() + rank.getName() + "&a now has " + args[2] + "this color&a."));
                                rank.setColor(args[2]);
                                rankManager.exportToDatabase(rank, true);
                            } else {
                                player.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "perms":
                        if(args.length > 1) {
                            rank = rankManager.getRankFromName(args[1]);
                            if(rank != null) {
                                String server = "_global";
                                if(args.length == 3) {
                                    if(!args[2].matches("[a-zA-Z]+")) {
                                        player.sendMessage(ChatColor.RED + "The server name you specified must only contain letters A-Z.");
                                        return true;
                                    }
                                    server = args[2].toLowerCase();
                                }

                                List<String> permissions = rank.getPermissions().get(server);
                                if(permissions != null) {
                                    Collections.sort(permissions);

                                    sb = new StringBuilder();
                                    sb.append("&aPermissions for " + rank.getColor() + rank.getName() + " &afor server type&f" + server + "&7:");

                                    for(String permission : permissions) {
                                        sb.append("\n&7+ &f");
                                        sb.append(permission);
                                    }

                                    player.sendMessage(Colors.get(sb.toString()));
                                } else {
                                    player.sendMessage(ChatColor.RED + "This rank does not have any permissions set for this server type.");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "addperm":
                        if(args.length > 2) {
                            rank = rankManager.getRankFromName(args[1]);
                            if(rank != null) {
                                String permission = args[2].toLowerCase();
                                String server = "_global";
                                if(args.length == 4) {
                                    server = args[3].toLowerCase();
                                }

                                rank.getPermissions().computeIfAbsent(server, k -> new ArrayList<>());
                                if(!rank.getPermissions().get(server).contains(permission)) {
                                    rank.getPermissions().get(server).add(permission);
                                    rankManager.exportToDatabase(rank, true);
                                    player.sendMessage(Colors.get("&aRank " + rank.getColor() + rank.getName() + "&a now has the permission &f" + permission + "&a set for server type &f" + server + "&a."));
                                } else {
                                    player.sendMessage(ChatColor.RED + "This rank already has the permission set for server type" + server + ".");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "delperm":
                        if(args.length > 2) {
                            rank = rankManager.getRankFromName(args[1]);
                            if(rank != null) {
                                String permission = args[2].toLowerCase();
                                String server = "_global";
                                if(args.length == 4) {
                                    server = args[3].toLowerCase();
                                }

                                rank.getPermissions().computeIfAbsent(server, k -> new ArrayList<>());
                                if(rank.getPermissions().get(server).contains(permission)) {
                                    rank.getPermissions().get(server).remove(permission);
                                    rankManager.exportToDatabase(rank, true);
                                    player.sendMessage(Colors.get("&aRank " + rank.getColor() + rank.getName() + "&a no longer the permission &f" + permission + "&a set for server type &f" + server + "&a."));
                                } else {
                                    player.sendMessage(ChatColor.RED + "This rank does not have this permission set for server type " + server + ".");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "parents":
                        if(args.length > 1) {
                            rank = rankManager.getRankFromName(args[1]);
                            if(rank != null) {
                                rankList = new ArrayList<>(rank.getParents(plugin));
                                Collections.sort(rankList);

                                sb = new StringBuilder();
                                sb.append("&6Parents of rank " + rank.getColor() + rank.getDisplayName() + " &7(" + rankList.size() + "):&f ");

                                while(!rankList.isEmpty()) {
                                    rank = rankList.get(0);
                                    sb.append(rank.getColor() + rank.getName() + " &7(" + rank.getWeight() + ")");

                                    rankList.remove(rank);

                                    if(rankList.isEmpty()) {
                                        sb.append(".");
                                    } else {
                                        sb.append(", ");
                                    }
                                }

                                player.sendMessage(Colors.get(sb.toString()));
                                return true;
                            } else {
                                player.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "addparent":
                        if(args.length > 2) {
                            rank = rankManager.getRankFromName(args[1]);
                            if(rank != null) {
                                Rank parentRank = rankManager.getRankFromName(args[2]);
                                if(parentRank != null) {
                                    if(rank.getParents().contains(parentRank.getUuid())) {
                                        player.sendMessage(ChatColor.RED + "This rank already has " + rank.getName() + " as a parent.");
                                        return true;
                                    }

                                    rank.getParents().add(parentRank.getUuid());
                                    rankManager.exportToDatabase(rank, true);
                                    player.sendMessage(Colors.get("&aRank " + rank.getColor() + rank.getName() + "&a now has the parent &f" + parentRank.getColor() + parentRank.getName() + "&a."));
                                } else {
                                    player.sendMessage(ChatColor.RED + "The parent rank you specified does not exist.");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "delparent":
                        if(args.length > 2) {
                            rank = rankManager.getRankFromName(args[1]);
                            if(rank != null) {
                                Rank parentRank = rankManager.getRankFromName(args[2]);
                                if(parentRank != null) {
                                    if(!rank.getParents().contains(parentRank.getUuid())) {
                                        player.sendMessage(ChatColor.RED + "This rank does not have " + rank.getName() + " as a parent.");
                                        return true;
                                    }

                                    rank.getParents().remove(parentRank.getUuid());
                                    rankManager.exportToDatabase(rank, true);
                                    player.sendMessage(Colors.get("&aRank " + rank.getColor() + rank.getName() + "&a no longer has the parent &f" + parentRank.getColor() + parentRank.getName()+ "&a."));
                                } else {
                                    player.sendMessage(ChatColor.RED + "The parent rank you specified does not exist.");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                            }
                            return true;
                        }
                        break;
                    case "namemc":
                        if(args.length > 2) {
                            rank = rankManager.getRankFromName(args[1]);
                            if(rank != null) {
                                boolean b = Boolean.parseBoolean(args[2]);
                                rank.setNameMcAward(b);
                                rankManager.exportToDatabase(rank, true);
                                player.sendMessage(Colors.get("&aRank " + rank.getColor() + rank.getName() + "&a NameMC reward status has been set to &f" + b + "&a."));
                            } else {
                                player.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                            }
                        }
                    case "assigned":
                        if(args.length > 1) {
                            rank = rankManager.getRankFromName(args[1]);
                            if(rank != null) {
                                Rank finalRank = rank;
                                plugin.getCoreProfileManager().getMongoManager().getCollection(true, plugin.getConfig().getString("networking.mongo.profiles_collection"), new MongoCollectionResult() {
                                    @Override
                                    public void call(MongoCollection<Document> mongoCollection) {
                                        Date requestStarted = new Date();
                                        List<String> names = new ArrayList<>();
                                        MongoCursor<Document> cursor = mongoCollection.find(new Document("ranks", finalRank.getUuid())).cursor();
                                        while(cursor.hasNext()) {
                                            names.add(cursor.next().getString("name"));
                                        }

                                        StringBuilder sb = new StringBuilder();
                                        sb.append("&6Players with rank " + finalRank.getColor() + finalRank.getName() + "&6: &f");

                                        Collections.sort(names);
                                        for(int x = 0; x < names.size(); x++) {
                                            String name = names.get(x);

                                            sb.append("&f");
                                            sb.append(name);
                                            if(x + 1 == names.size()) {
                                                sb.append("&7.");
                                            } else {
                                                sb.append("&7, ");
                                            }
                                        }

                                        Bukkit.getServer().getPluginManager().callEvent(new MongoMessageEvent(sb.toString(), player, requestStarted));
                                    }
                                });
                            } else {
                                player.sendMessage(ChatColor.RED + "The rank you specified does not exist.");
                            }

                            return true;
                        }
                        break;
                }
            }

            StringBuilder help = new StringBuilder();
            help.append("&6&l/rank &r&6Help");
            help.append("\n&7<> Required, [] Optional");
            help.append("\n&6/rank list &7- &fReturns the list of ranks in weight order.");
            help.append("\n&6/rank create <name> <weight> &7- &fCreates a new rank.");
            help.append("\n&6/rank delete <name> &7- &fDeletes an existing rank.");
            help.append("\n&6/rank rename <name> <new name> &7- &fRenames a rank.");
            help.append("\n&6/rank weight <name> <weight> &7- &fSets a rank weight.");
            help.append("\n&6/rank displayname <name> <display name> &7- &fSets a rank display name.");
            help.append("\n&6/rank prefix <name> <prefix> &7- &fSets a rank prefix.");
            help.append("\n&6/rank color <name> <color codes> &7- &fSets a rank color.");
            help.append("\n&6/rank perms <name> [server] &7- &fView permissions for a rank.");
            help.append("\n&6/rank addperm <name> <permission> [server] &7- &fAdds a permission to a rank.");
            help.append("\n&6/rank delperm <name> <permission> [server] &7- &fRemoves a permission from a rank.");
            help.append("\n&6/rank parents <name> &7- &fView parents of a rank.");
            help.append("\n&6/rank addparent <name> <parent> &7- &fAdds a parent to a rank.");
            help.append("\n&6/rank delparent <name> <parent> &7- &fRemoves a parent from a rank.");
            help.append("\n&6/rank namemc <name> <boolean> &7- &fDefines if a rank is a NameMC reward or not.");
            help.append("\n&6/rank assigned <name> &7- &fShows users that are assigned this rank.");

            player.sendMessage(Colors.get(help.toString()));
        }

        return true;
    }
}
