package camp.pvp.core.commands;

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

public class TempMuteCommand implements CommandExecutor {

    private SpigotCore plugin;
    public TempMuteCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("tempmute").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 2) {
            String target = args[0];
            if(!target.matches("^[a-zA-Z0-9_]{1,16}$")) {
                sender.sendMessage(ChatColor.RED + "Invalid username provided.");
                return true;
            }

            CoreProfile targetProfile = plugin.getCoreProfileManager().find(target, false);

            if(targetProfile != null) {
                Punishment mute = targetProfile.getActivePunishment(Punishment.Type.MUTE);
                if(mute == null) {
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

                    mute = new Punishment(UUID.randomUUID());
                    mute.setType(Punishment.Type.MUTE);
                    mute.setIp(targetProfile.getIp());
                    mute.setIssuedTo(targetProfile.getUuid());
                    mute.setIssuedToName(targetProfile.getName());
                    mute.setExpires(calendar.getTime());
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
                    if(args.length > 3) {
                        for(int i = 3; i < args.length; i++) {
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

                    if(reasonBuilder.length() == 0 || args.length < 4) {
                        reasonBuilder.append("No reason specified.");
                    }

                    mute.setSilent(silent);
                    mute.setReason(reasonBuilder.toString());

                    targetProfile.getPunishments().add(mute);
                    plugin.getPunishmentManager().exportToDatabase(mute, true);
                    plugin.getCoreProfileManager().exportToDatabase(targetProfile, true, false);

                    String banMessage = "&f" + targetProfile.getHighestRank().getColor() + target + "&a has been temporarily muted by " + issueFromColor + issueFromName + "&a.";
                    if(silent) {
                        plugin.getCoreProfileManager().staffBroadcast(banMessage);
                    } else {
                        Bukkit.broadcastMessage(Colors.get(banMessage));
                    }
                } else if(sender instanceof Player){
                    Player player = (Player) sender;
                    TextComponent text = new TextComponent(ChatColor.RED + targetProfile.getName() + " is already muted, click this message to view details.");
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/punishmentdetails " + mute.getUuid().toString()));
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "/pd " + mute.getUuid().toString()).create()));
                    player.spigot().sendMessage(text);
                } else {
                    sender.sendMessage(ChatColor.RED + targetProfile.getName() + " is already banned.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /tempmute <player> <time> <format> [reason] [-s] [-ip]");
        }

        return true;
    }
}
