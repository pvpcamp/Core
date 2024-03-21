package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UnmuteCommand implements CommandExecutor {

    private Core plugin;
    public UnmuteCommand(Core plugin) {
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("unmute");
        command.setExecutor(this);
        command.setTabCompleter(new PlayerTabCompleter());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player> [reason] [-s]");
            return true;
        }

        CompletableFuture<CoreProfile> profileFuture = plugin.getCoreProfileManager().findAsync(args[0]);
        profileFuture.thenAccept(profile -> {
            if(profile == null) {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                return;
            }

            Punishment punishment = profile.getActivePunishment(Punishment.Type.MUTE);
            if(punishment == null) {
                sender.sendMessage(ChatColor.RED + profile.getName() + " is not currently muted.");
                return;
            }

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

            punishment.setPardonerName(issueFromName);
            punishment.setPardoner(issuedFrom);
            punishment.setPardoned(new Date());

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

            punishment.setSilent(silent);
            punishment.setPardonReason(reasonBuilder.toString());

            plugin.getPunishmentManager().exportToDatabase(punishment);

            String targetName = profile.getHighestRank().getColor() + profile.getName();
            String punishmentMessage = "&f" + targetName + "&a has been unmuted by " + issueFromColor + issueFromName + "&a.";
            if(silent) {
                plugin.getCoreProfileManager().staffBroadcast(punishmentMessage);
            } else {
                Bukkit.broadcastMessage(Colors.get(punishmentMessage));
            }
        });

        return true;
    }
}
