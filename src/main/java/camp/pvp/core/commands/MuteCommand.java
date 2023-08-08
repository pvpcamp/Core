package camp.pvp.core.commands;

import camp.pvp.core.Core;
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

public class MuteCommand implements CommandExecutor {

    private Core plugin;
    public MuteCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("mute").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0) {
            String target = args[0];

            CoreProfile targetProfile = plugin.getCoreProfileManager().find(target, false);

            if(targetProfile != null) {
                Punishment mute = targetProfile.getActivePunishment(Punishment.Type.MUTE);
                if(mute == null) {
                    mute = new Punishment(UUID.randomUUID());
                    mute.setType(Punishment.Type.MUTE);
                    mute.setIp(targetProfile.getIp());
                    mute.setIssuedTo(targetProfile.getUuid());
                    mute.setIssuedToName(targetProfile.getName());
                    mute.setIssued(new Date());

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

                    mute.setIssuedFrom(issuedFrom);
                    mute.setIssuedFromName(issueFromName);

                    StringBuilder reasonBuilder = new StringBuilder();
                    boolean silent = false;
                    if(args.length > 1) {
                        for(int i = 1; i < args.length; i++) {
                            switch(args[i]) {
                                case "-s":
                                    silent = true;
                                    break;
                                case "-ip":
                                    mute.setIpPunished(true);
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

                    mute.setSilent(silent);
                    mute.setReason(reasonBuilder.toString());

                    targetProfile.getPunishments().add(mute);
                    plugin.getPunishmentManager().exportToDatabase(mute, true);
                    plugin.getCoreProfileManager().exportToDatabase(targetProfile, true, false);

                    String targetName = targetProfile.getHighestRank().getColor() + targetProfile.getName();
                    String punishMessage = "&f" + targetName + "&a has been permanently muted by " + issueFromColor + issueFromName + "&a.";
                    if(silent) {
                        plugin.getCoreProfileManager().staffBroadcast(punishMessage);
                    } else {
                        Bukkit.broadcastMessage(Colors.get(punishMessage));
                    }
                } else if(sender instanceof Player){
                    Player player = (Player) sender;
                    TextComponent text = new TextComponent(ChatColor.RED + targetProfile.getName() + " is already muted, click this message to view details.");
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/punishmentdetails " + mute.getUuid().toString()));
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "/pd " + mute.getUuid().toString()).create()));
                    player.spigot().sendMessage(text);
                } else {
                    sender.sendMessage(ChatColor.RED + targetProfile.getName() + " is already muted.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /mute <player> [reason] [-s] [-ip]");
        }

        return true;
    }
}
