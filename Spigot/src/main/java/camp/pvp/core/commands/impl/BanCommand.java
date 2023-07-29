package camp.pvp.core.commands.impl;

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

import java.util.Date;
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

            CoreProfile targetProfile = plugin.getCoreProfileManager().find(target, false);

            if(targetProfile != null) {
                Punishment ban = targetProfile.getActivePunishment(Punishment.Type.BAN);
                if(ban == null) {
                    ban = new Punishment(UUID.randomUUID());
                    ban.setType(Punishment.Type.BAN);
                    ban.setIp(targetProfile.getIp());
                    ban.setIssued(new Date());
                    ban.setIssuedTo(targetProfile.getUuid());
                    ban.setIssuedToName(targetProfile.getName());

                    String issueFromName = sender.getName();
                    String issueFromColor = "&4";
                    UUID issuedFrom = null;
                    if(sender instanceof Player) {
                        Player player = (Player) sender;
                        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
                        issueFromColor = profile.getHighestRank().getColor();
                        issueFromName = profile.getName();
                        issuedFrom = player.getUniqueId();
                    }

                    ban.setIssuedFrom(issuedFrom);
                    ban.setIssuedFromName(issueFromName);

                    StringBuilder reasonBuilder = new StringBuilder();
                    boolean silent = false;
                    if(args.length > 1) {
                        for(int i = 1; i < args.length; i++) {
                            switch(args[i]) {
                                case "-s":
                                    silent = true;
                                    break;
                                case "-ip":
                                    ban.setIpPunished(true);
                                    break;
                                default:
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

                    String targetName = targetProfile.getHighestRank().getColor() + targetProfile.getName();
                    String banMessage = "&f" + targetName + "&a has been permanently banned by " + issueFromColor + issueFromName + "&a.";
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
