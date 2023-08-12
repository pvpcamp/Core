package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.utils.Colors;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
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

public class BlacklistCommand implements CommandExecutor {

    private Core plugin;
    public BlacklistCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("blacklist").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0) {
            String target = args[0];

            CoreProfile targetProfile = plugin.getCoreProfileManager().find(target, false);

            if(targetProfile != null) {
                Punishment blacklist = targetProfile.getActivePunishment(Punishment.Type.BLACKLIST);
                if(blacklist == null) {

                    StringBuilder reasonBuilder = new StringBuilder();
                    boolean silent = false;
                    if(args.length > 1) {
                        for(int i = 1; i < args.length; i++) {
                            switch(args[i]) {
                                case "-s":
                                    silent = true;
                                    break;
                                default:
                                    reasonBuilder.append(args[i]);

                                    if(i + 1 != args.length) {
                                        reasonBuilder.append(" ");
                                    }
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You must supply a reason.");
                        return true;
                    }

                    blacklist = new Punishment(UUID.randomUUID());
                    blacklist.setType(Punishment.Type.BLACKLIST);
                    blacklist.setIssuedTo(targetProfile.getUuid());
                    blacklist.setIssuedToName(targetProfile.getName());
                    blacklist.setIssued(new Date());

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

                    blacklist.setIssuedFrom(issuedFrom);
                    blacklist.setIssuedFromName(issueFromName);

                    blacklist.setIp(targetProfile.getIp());
                    blacklist.setIpPunished(true);

                    blacklist.setSilent(silent);
                    blacklist.setReason(reasonBuilder.toString());

                    targetProfile.getPunishments().add(blacklist);
                    plugin.getPunishmentManager().exportToDatabase(blacklist, true);
                    plugin.getCoreProfileManager().exportToDatabase(targetProfile, true, false);

                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("KickPlayer");
                    out.writeUTF(args[0]);
                    out.writeUTF(Colors.get("&4You have been blacklisted from PvP Camp."));

                    plugin.getServer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());

                    String targetName = targetProfile.getHighestRank().getColor() + targetProfile.getName();
                    String banMessage = "&f" + targetName + "&a has been permanently blacklisted by " + issueFromColor + issueFromName + "&a.";
                    if(silent) {
                        plugin.getCoreProfileManager().staffBroadcast(banMessage);
                    } else {
                        Bukkit.broadcastMessage(Colors.get(banMessage));
                    }
                } else if(sender instanceof Player){
                    Player player = (Player) sender;
                    TextComponent text = new TextComponent(ChatColor.RED + targetProfile.getName() + " is already blacklisted, click this message to view player history.");
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/history " + targetProfile.getName()));
                    player.spigot().sendMessage(text);
                } else {
                    sender.sendMessage(ChatColor.RED + targetProfile.getName() + " is already blacklisted.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /blacklist <player> [reason] [-s]");
        }

        return true;
    }
}
