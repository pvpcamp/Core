package camp.pvp.core.commands.punishments;

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

public class UnblacklistCommand implements CommandExecutor {

    private SpigotCore plugin;
    public UnblacklistCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("unblacklist").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0) {
            String target = args[0].replaceAll("\\$[A-Za-z0-9]+(_[A-Za-z0-9]+)*\\$", "");
            CoreProfile targetProfile = plugin.getCoreProfileManager().find(target, false);

            if(targetProfile != null) {
                Punishment blacklist = targetProfile.getActivePunishment(Punishment.Type.BLACKLIST);
                if(blacklist != null) {
                    String issueFromName = sender.getName();
                    UUID issuedFrom = null;
                    if(sender instanceof Player) {
                        Player player = (Player) sender;
                        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
                        issueFromName = profile.getHighestRank().getColor() + profile.getName();
                        issuedFrom = player.getUniqueId();
                    }

                    blacklist.setPardoner(issuedFrom);

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

                    blacklist.setSilent(silent);
                    blacklist.setPardonReason(reasonBuilder.toString());
                    blacklist.setPardoned(new Date());

                    plugin.getPunishmentManager().exportToDatabase(blacklist, true);
                    plugin.getCoreProfileManager().exportToDatabase(targetProfile, true, false);

                    String punishmentMessage = "&f" + targetProfile.getHighestRank().getColor() + target + "&a has been unblacklisted by " + issueFromName + "&a.";
                    if(silent) {
                        plugin.getCoreProfileManager().staffBroadcast(punishmentMessage);
                    } else {
                        Bukkit.broadcastMessage(Colors.get(punishmentMessage));
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + targetProfile.getName() + " is not currently blacklisted.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage /unblacklist <player> [reason] [-s]");
        }

        return true;
    }
}
