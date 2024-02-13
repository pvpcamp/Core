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
import java.util.concurrent.CompletableFuture;

public class BanCommand implements CommandExecutor {

    private Core plugin;
    public BanCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("ban").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player> [reason] [-s] [-ip]");
            return true;
        }

        CompletableFuture<CoreProfile> profileFuture = plugin.getCoreProfileManager().findAsync(args[0]);
        profileFuture.thenAccept(profile -> {
            if (profile == null) {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                return;
            }

            if(profile.getActivePunishment(Punishment.Type.BAN) != null) {
                sender.sendMessage(ChatColor.RED + profile.getName() + " is already banned.");
                return;
            }

            Punishment punishment = new Punishment(UUID.randomUUID());
            punishment.setType(Punishment.Type.BAN);
            punishment.setIps(profile.getIpList());
            punishment.setIssuedTo(profile.getUuid());
            punishment.setIssuedToName(profile.getName());
            punishment.setIssued(new Date());

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

            punishment.setIssuedFromName(issueFromName);
            punishment.setIssuedFrom(issuedFrom);

            StringBuilder reasonBuilder = new StringBuilder();
            boolean silent = false;
            if(args.length > 1) {
                for(int i = 1; i < args.length; i++) {
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

            if(reasonBuilder.length() == 0 || args.length < 2) {
                reasonBuilder.append("No reason specified.");
            }

            punishment.setSilent(silent);
            punishment.setReason(reasonBuilder.toString());
            plugin.getPunishmentManager().exportToDatabase(punishment);

            String targetName = profile.getHighestRank().getColor() + profile.getName();
            String punishMessage = "&f" + targetName + "&a has been permanently banned by " + issueFromColor + issueFromName + "&a.";
            if(silent) {
                plugin.getCoreProfileManager().staffBroadcast(punishMessage);
            } else {
                Bukkit.broadcastMessage(Colors.get(punishMessage));
            }
        });

        return true;
    }
}
