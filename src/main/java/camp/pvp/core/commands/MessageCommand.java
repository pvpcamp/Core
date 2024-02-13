package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.ChatHistory;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.utils.ChatUtils;
import camp.pvp.core.utils.Colors;
import camp.pvp.core.utils.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class MessageCommand implements CommandExecutor {

    private Core plugin;
    public MessageCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("message").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            Player target = null;
            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
            CoreProfile targetProfile;
            StringBuilder message = new StringBuilder();

            Punishment mute = profile.getActivePunishment(Punishment.Type.MUTE);
            if(mute != null) {
                player.sendMessage(Colors.get(mute.getType().getMessage()));
                player.sendMessage(Colors.get("&cReason: &f" + mute.getReason()));
                player.sendMessage(Colors.get("&cExpires: &f" + (mute.getExpires() == null ? "Never" : DateUtils.getDifference(mute.getExpires(), new Date()))));
                player.sendMessage(Colors.get(mute.getType().getAppealMessage()));
                return true;
            }

            int start;
            switch (label.toLowerCase()) {
                case "r":
                case "reply":
                    if(args.length > 0) {
                        if (profile.getReplyTo() != null) {
                            target = Bukkit.getPlayer(profile.getReplyTo());
                        }

                        start = 0;
                    } else {
                        player.sendMessage(ChatColor.RED + "Usage: /" + label + " <message>");
                        return true;
                    }
                    break;
                case "m":
                case "pm":
                case "msg":
                case "message":
                case "tell":
                case "whisper":
                    if (args.length > 1) {
                        target = Bukkit.getPlayer(args[0]);
                        start = 1;
                    } else {
                        player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player> <message>");
                        return true;
                    }
                    break;
                default:
                    return true;
            }

            if(target != null) {
                if(target.equals(player)) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Why?");
                    return true;
                }
                targetProfile = plugin.getCoreProfileManager().getLoadedProfiles().get(target.getUniqueId());

                for (int i = start; i < args.length; i++) {
                    message.append(args[i]);
                    if (i + 1 != args.length) {
                        message.append(" ");
                    }
                }

                if(!targetProfile.getIgnored().contains(player.getUniqueId())) {

                    if(!targetProfile.isAllowPrivateMessages()) {
                        player.sendMessage(ChatColor.RED + "You cannot message " + target.getName() + " right now.");
                        return true;
                    }

                    if(profile.getIgnored().contains(targetProfile.getUuid())) {
                        player.sendMessage(ChatColor.RED + "You cannot message " + target.getName() + " because you ignored them.");
                        return true;
                    }

                    profile.setReplyTo(target.getUniqueId());
                    player.sendMessage(Colors.get("&7(To " + targetProfile.getHighestRank().getColor() + target.getName() + "&7) &f" + message.toString()));

                    boolean filtered = false;
                    if(ChatUtils.isFiltered(message.toString())) {
                        if(!player.hasPermission("core.chat.bypass.filter")) {
                            plugin.getCoreServerManager().sendStaffMessage("&c[Filtered] &7(" + plugin.getCoreServerManager().getCoreServer().getName() + "&7) &f" + player.getName() + " -> " + target.getName() + "&7: &f" + message.toString());
                            filtered = true;
                        } else {
                            player.sendMessage(ChatColor.RED + "Your message would have been filtered.");
                        }
                    }

                    ChatHistory chatHistory = new ChatHistory(
                            UUID.randomUUID(),
                            player.getUniqueId(),
                            player.getName(),
                            target.getName() + " -> " + message.toString(),
                            plugin.getCoreServerManager().getCoreServer().getName(),
                            ChatHistory.Type.PRIVATE_MESSAGE,
                            new Date(),
                            filtered);
                    plugin.getCoreProfileManager().exportHistory(chatHistory);

                    if(!filtered) {
                        targetProfile.setReplyTo(player.getUniqueId());
                        target.sendMessage(Colors.get("&7(From " + profile.getHighestRank().getColor() + player.getName() + "&7) &f" + message.toString()));

                        if(targetProfile.isMessageSounds()) {
                            target.playSound(target.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot message " + target.getName() + " right now.");
                }

            } else {
                player.sendMessage(ChatColor.RED + "The player you are trying to message is not online.");
            }

        }

        return true;
    }
}
