package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {

    private Core plugin;
    public KickCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("kick").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length > 0) {
            Player target = Bukkit.getPlayer(args[0]);
            if(target != null) {
                CoreProfile targetProfile = plugin.getCoreProfileManager().getLoadedProfiles().get(target.getUniqueId());

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

                String issueFromName = sender.getName();
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
                    issueFromName = profile.getHighestRank().getColor() + profile.getName();
                }

                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("KickPlayer");
                out.writeUTF(args[0]);
                out.writeUTF(Colors.get("&cYou have been kicked from PvP Camp." + "\n&cReason: &f" + reasonBuilder));

                plugin.getServer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());

                String targetName = targetProfile.getHighestRank().getColor() + targetProfile.getName();
                String banMessage = "&f" + targetName + "&a has been kicked by " + issueFromName + "&a.";
                if(silent) {
                    plugin.getCoreProfileManager().staffBroadcast(banMessage);
                } else {
                    Bukkit.broadcastMessage(Colors.get(banMessage));
                }
            } else {
                sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /kick <player> [reason] [-s]");
        }

        return true;
    }
}
