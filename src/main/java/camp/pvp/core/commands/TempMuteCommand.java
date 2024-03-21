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
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TempMuteCommand implements CommandExecutor {

    private Core plugin;
    public TempMuteCommand(Core plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("tempmute");
        command.setExecutor(this);
        command.setTabCompleter(new PlayerTabCompleter());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player> <time> <format> [reason] [-s] [-ip]");
            return true;
        }

        CompletableFuture<CoreProfile> profileFuture = plugin.getCoreProfileManager().findAsync(args[0]);
        profileFuture.thenAccept(profile -> {
            if(profile == null) {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                return;
            }

            Punishment punishment = profile.getActivePunishment(Punishment.Type.MUTE);
            if(punishment != null) {
                sender.sendMessage(ChatColor.RED + profile.getName() + " is already muted.");
                return;
            }

            int duration;
            try {
                duration = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
                sender.sendMessage(ChatColor.RED + "Invalid format.");
                return;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());

            switch(args[2].toLowerCase()) {
                case "s":
                case "sec":
                case "seconds":
                    calendar.add(Calendar.SECOND, duration);
                    break;
                case "m":
                case "min":
                case "mins":
                case "minute":
                case "minutes":
                    calendar.add(Calendar.MINUTE, duration);
                    break;
                case "h":
                case "hr":
                case "hour":
                case "hours":
                    calendar.add(Calendar.HOUR, duration);
                    break;
                case "d":
                case "day":
                case "days":
                    calendar.add(Calendar.DAY_OF_YEAR, duration);
                    break;
                case "w":
                case "week":
                case "weeks":
                    calendar.add(Calendar.WEEK_OF_YEAR, duration);
                    break;
                case "month":
                case "months":
                    calendar.add(Calendar.MONTH, duration);
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "Invalid duration format.");
                    sender.sendMessage(ChatColor.RED + "Valid options: seconds, minutes, hours, days, weeks, months.");
                    return;
            }

            punishment = new Punishment(UUID.randomUUID());
            punishment.setType(Punishment.Type.MUTE);
            punishment.setIps(profile.getIpList());
            punishment.setIssuedTo(profile.getUuid());
            punishment.setIssuedToName(profile.getName());
            punishment.setIssued(new Date());
            punishment.setExpires(calendar.getTime());

            String issueFromName = sender.getName();
            String issueFromColor = "&4";
            UUID issuedFrom = null;

            if(sender instanceof Player) {
                Player player = (Player) sender;
                CoreProfile senderProfile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
                issueFromColor = senderProfile.getHighestRank().getColor();
                issueFromName = senderProfile.getName();
                issuedFrom = senderProfile.getUuid();
            }

            punishment.setIssuedFrom(issuedFrom);
            punishment.setIssuedFromName(issueFromName);

            StringBuilder reasonBuilder = new StringBuilder();
            boolean silent = false;

            if(args.length > 3) {
                for(int i = 3; i < args.length; i++) {
                    switch(args[i]) {
                        case "-s":
                            silent = true;
                            break;
                        case "-ip":
                            punishment.setIpPunished(true);
                            break;
                        default:
                            reasonBuilder.append(args[i]);

                            if(i + 1 != args.length) {
                                reasonBuilder.append(" ");
                            }
                    }
                }
            }

            if(reasonBuilder.length() == 0 || args.length < 4) {
                reasonBuilder.append("No reason specified.");
            }

            punishment.setSilent(silent);
            punishment.setReason(reasonBuilder.toString());
            plugin.getPunishmentManager().exportToDatabase(punishment);

            String targetName = profile.getHighestRank().getColor() + profile.getName();
            String punishmentMessage = "&f" + targetName + "&a has been temporarily muted by " + issueFromColor + issueFromName + "&a.";
            if(silent) {
                plugin.getCoreProfileManager().staffBroadcast(punishmentMessage);
            } else {
                Bukkit.broadcastMessage(Colors.get(punishmentMessage));
            }
        });

        return true;
    }
}
