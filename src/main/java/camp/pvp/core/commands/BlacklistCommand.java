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

public class BlacklistCommand implements CommandExecutor {

    private Core plugin;
    public BlacklistCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("blacklist").setExecutor(this);
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

            if(profile.getActivePunishment(Punishment.Type.BLACKLIST) != null) {
                sender.sendMessage(ChatColor.RED + profile.getName() + " is already blacklisted.");
                return;
            }

            Punishment punishment = new Punishment(UUID.randomUUID());
            punishment.setType(Punishment.Type.BLACKLIST);
            punishment.setIps(profile.getIpList());
            punishment.setIssuedTo(profile.getUuid());
            punishment.setIssuedToName(profile.getName());
            punishment.setIssued(new Date());
            punishment.setIpPunished(true);

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

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("KickPlayer");
            out.writeUTF(args[0]);
            out.writeUTF(Colors.get("&cYou have been blacklisted from PvP Camp."));

            punishment.setSilent(silent);
            punishment.setReason(reasonBuilder.toString());
            plugin.getPunishmentManager().exportToDatabase(punishment);

            String targetName = profile.getHighestRank().getColor() + profile.getName();
            String punishMessage = "&f" + targetName + "&4 has been permanently blacklisted by " + issueFromColor + issueFromName + "&4.";
            if(silent) {
                plugin.getCoreProfileManager().staffBroadcast(punishMessage);
            } else {
                Bukkit.broadcastMessage(Colors.get(punishMessage));
            }
        });

        return true;
    }
}
