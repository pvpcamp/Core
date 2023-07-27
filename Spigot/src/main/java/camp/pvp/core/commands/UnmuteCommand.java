package camp.pvp.core.commands;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class UnmuteCommand implements CommandExecutor {

    private SpigotCore plugin;
    public UnmuteCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("unmute").setExecutor(this);
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
                Punishment mute = targetProfile.getActivePunishment(Punishment.Type.MUTE);
                if(mute != null) {
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

                    mute.setPardonerName(issueFromName);
                    mute.setPardoner(issuedFrom);

                    StringBuilder reasonBuilder = new StringBuilder();
                    boolean silent = false;
                    if(args.length > 1) {
                        for(int i = 1; i < args.length; i++) {
                            if(args[i].equalsIgnoreCase("-s")) {
                                silent = true;
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

                    mute.setSilent(silent);
                    mute.setPardonReason(reasonBuilder.toString());
                    mute.setPardoned(new Date());

                    plugin.getPunishmentManager().exportToDatabase(mute, true);
                    plugin.getCoreProfileManager().exportToDatabase(targetProfile, true, false);

                    String punishmentMessage = "&f" + targetProfile.getHighestRank().getColor() + target + "&a has been unmuted by " + issueFromColor + issueFromName + "&a.";
                    if(silent) {
                        plugin.getCoreProfileManager().staffBroadcast(punishmentMessage);
                    } else {
                        Bukkit.broadcastMessage(Colors.get(punishmentMessage));
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + targetProfile.getName() + " is not currently muted.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage /unmute <player> [reason] [-s]");
        }

        return true;
    }
}
