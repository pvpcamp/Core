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

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class TempBanCommand implements CommandExecutor {

    private SpigotCore plugin;
    public TempBanCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("tempban").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 2) {
            String target = args[0].replaceAll("\\$[A-Za-z0-9]+(_[A-Za-z0-9]+)*\\$", "");
            CoreProfile targetProfile = plugin.getCoreProfileManager().find(target, false);

            if(targetProfile != null) {
                Punishment ban = targetProfile.getActivePunishment(Punishment.Type.BAN);
                if(ban == null) {
                    int duration;
                    try {
                        duration = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {
                        sender.sendMessage(ChatColor.RED + "Invalid format.");
                        return true;
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
                            return true;
                    }

                    ban = new Punishment(UUID.randomUUID());
                    ban.setType(Punishment.Type.BAN);
                    ban.setIp(targetProfile.getIp());
                    ban.setIssuedTo(targetProfile.getUuid());
                    ban.setExpires(calendar.getTime());

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
                    if(args.length > 3) {
                        for(int i = 1; i < args.length; i++) {
                            if(args[3].equalsIgnoreCase("-s")) {
                                silent = true;
                            } else {
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

                    ban.setSilent(silent);
                    ban.setReason(reasonBuilder.toString());

                    targetProfile.getPunishments().add(ban);
                    plugin.getPunishmentManager().exportToDatabase(ban, true);
                    plugin.getCoreProfileManager().exportToDatabase(targetProfile, true, false);

                    if(targetProfile.getPlayer() != null) {
                        targetProfile.getPlayer().kickPlayer(Colors.get("&cYou have been temporarily banned from PvP Camp."));
                    }

                    String banMessage = "&f" + targetProfile.getHighestRank().getColor() + target + "&a has been temporarily banned by " + issueFromName + "&a.";
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
            sender.sendMessage(ChatColor.RED + "Usage: /tempban <player> <time> <format> [reason] [-s]");
        }

        return true;
    }
}
