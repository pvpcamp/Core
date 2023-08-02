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

public class UnbanCommand implements CommandExecutor {

    private SpigotCore plugin;
    public UnbanCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("unban").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length > 0) {
            String target = args[0];

            CoreProfile targetProfile = plugin.getCoreProfileManager().find(target, false);

            if(targetProfile != null) {
                Punishment ban = targetProfile.getActivePunishment(Punishment.Type.BAN);
                if(ban != null) {
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

                    ban.setPardonerName(issueFromName);
                    ban.setPardoner(issuedFrom);

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

                    ban.setSilent(silent);
                    ban.setPardonReason(reasonBuilder.toString());
                    ban.setPardoned(new Date());

                    plugin.getPunishmentManager().exportToDatabase(ban, true);
                    plugin.getCoreProfileManager().exportToDatabase(targetProfile, true, false);

                    String targetName = targetProfile.getHighestRank().getColor() + targetProfile.getName();
                    String punishmentMessage = "&f" + targetName + "&a has been unbanned by " + issueFromColor + issueFromName + "&a.";
                    if(silent) {
                        plugin.getCoreProfileManager().staffBroadcast(punishmentMessage);
                    } else {
                        Bukkit.broadcastMessage(Colors.get(punishmentMessage));
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + targetProfile.getName() + " is not currently banned.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage /unban <player> [reason] [-s]");
        }

        return true;
    }
}