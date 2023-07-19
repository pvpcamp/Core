package camp.pvp.core.commands.punishments;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.utils.Colors;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BanCommand implements CommandExecutor {

    private SpigotCore plugin;
    public BanCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("ban").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length > 0) {
            String target = args[0];
            if(!target.matches("^[a-zA-Z0-9_]{1,16}$")) {
                sender.sendMessage(ChatColor.RED + "Invalid username provided.");
                return true;
            }

            CoreProfile targetProfile = plugin.getCoreProfileManager().find(target, false);

            if(targetProfile != null) {
                Punishment ban = targetProfile.getActivePunishment(Punishment.Type.BAN);
                if(ban == null) {
                    ban = new Punishment(UUID.randomUUID());
                    ban.setType(Punishment.Type.BAN);
                    ban.setIp(targetProfile.getIp());
                    ban.setIssuedTo(targetProfile.getUuid());

                    String issueFromName = sender.getName();
                    UUID issuedFrom = null;
                    if(sender instanceof Player) {
                        Player player = (Player) sender;
                        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
                        issueFromName = profile.getHighestRank().getColor() + profile.getName();
                        issuedFrom = player.getUniqueId();
                    }

                    ban.setIssuedFrom(issuedFrom);

                    StringBuilder reasonBuilder = new StringBuilder();
                    boolean silent = false;
                    if(args.length > 1) {
                        for(int i = 1; i < args.length; i++) {
                            if(args[i].equalsIgnoreCase("-s")) {
                                silent = true;
                            } else if(args[i].equalsIgnoreCase("-ip")) {
                                ban.setIpPunished(true);
                            } else {
                                reasonBuilder.append(args[i]);

                                if(i + 1 != args.length) {
                                    reasonBuilder.append(" ");
                                }
                            }
                        }
                    }

                    if(reasonBuilder.length() == 0 || args.length < 2) {
                        reasonBuilder.append("No reason specified.");
                    }

                    ban.setSilent(silent);
                    ban.setReason(reasonBuilder.toString());

                    targetProfile.getPunishments().add(ban);
                    plugin.getPunishmentManager().exportToDatabase(ban, true);
                    plugin.getCoreProfileManager().exportToDatabase(targetProfile, true, false);

                    if(targetProfile.getPlayer() != null) {
                        targetProfile.getPlayer().kickPlayer(Colors.get("&cYou have been banned from PvP Camp."));
                    }

                    String banMessage = "&f" + targetProfile.getHighestRank().getColor() + target + "&a has been permanently banned by " + issueFromName + "&a.";
                    if(silent) {
                        plugin.getCoreProfileManager().staffBroadcast(banMessage);
                    } else {
                        Bukkit.broadcastMessage(Colors.get(banMessage));
                    }
                } else if(sender instanceof Player){
                    Player player = (Player) sender;
                    TextComponent text = new TextComponent(ChatColor.RED + targetProfile.getName() + " is already banned, click this message to view details.");
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/punishmentdetails " + ban.getUuid().toString()));
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "/pd " + ban.getUuid().toString()).create()));
                    player.spigot().sendMessage(text);
                } else {
                    sender.sendMessage(ChatColor.RED + targetProfile.getName() + " is already banned.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /ban <player> [reason] [-s] [-ip]");
        }

        return true;
    }
}
