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

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TempBanCommand implements CommandExecutor {

    private Core plugin;

    public TempBanCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("tempban").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player> <time> <format> [reason] [-s] [-ip]");
            return true;
        }

        CompletableFuture<CoreProfile> profileFuture = plugin.getCoreProfileManager().findAsync(args[0]);
        profileFuture.thenAccept(profile -> {
            if (profile == null) {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                return;
            }

            Punishment punishment = profile.getActivePunishment(Punishment.Type.BAN);
            if (punishment != null) {
                sender.sendMessage(ChatColor.RED + profile.getName() + " is already banned.");
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

            switch (args[2].toLowerCase()) {
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
            punishment.setType(Punishment.Type.BAN);
            punishment.setIps(profile.getIpList());
            punishment.setIssuedTo(profile.getUuid());
            punishment.setIssuedToName(profile.getName());
            punishment.setIssued(new Date());
            punishment.setExpires(calendar.getTime());

            String issueFromName = sender.getName();
            String issueFromColor = "&4";
            UUID issuedFrom = null;

            if (sender instanceof Player) {
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

            if (args.length > 3) {
                for (int i = 3; i < args.length; i++) {
                    switch (args[i]) {
                        case "-s":
                            silent = true;
                            break;
                        case "-ip":
                            punishment.setIpPunished(true);
                            break;
                        default:
                            reasonBuilder.append(args[i]);

                            if (i + 1 != args.length) {
                                reasonBuilder.append(" ");
                            }
                    }
                }
            }

            if (reasonBuilder.length() == 0 || args.length < 4) {
                reasonBuilder.append("No reason specified.");
            }

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("KickPlayer");
            out.writeUTF(args[0]);
            out.writeUTF(Colors.get("&cYou have been temporarily banned from PvP Camp."));
            plugin.getServer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());

            punishment.setSilent(silent);
            punishment.setReason(reasonBuilder.toString());
            plugin.getPunishmentManager().exportToDatabase(punishment);

            String targetName = profile.getHighestRank().getColor() + profile.getName();
            String punishmentMessage = "&f" + targetName + "&a has been temporarily banned by " + issueFromColor + issueFromName + "&a.";
            if (silent) {
                plugin.getCoreProfileManager().staffBroadcast(punishmentMessage);
            } else {
                Bukkit.broadcastMessage(Colors.get(punishmentMessage));
            }
        });

        return true;
    }
}