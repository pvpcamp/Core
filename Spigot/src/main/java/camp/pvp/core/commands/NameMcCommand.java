package camp.pvp.core.commands;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NameMcCommand implements CommandExecutor {

    private SpigotCore plugin;
    public NameMcCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("namemc").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
            if(profile != null) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (!profile.canUseCommand("/namemc")) {
                            player.sendMessage(ChatColor.RED + "Please wait before using this command again.");
                            return;
                        }

                        boolean namemc = false;
                        try {
                            InputStream input = new URL("https://api.namemc.com/server/" + plugin.getConfig().getString("namemc.api_server") + "/likes?profile=" + profile.getUuid().toString()).openStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
                            StringBuilder stringBuilder = new StringBuilder();
                            int cp;
                            while ((cp = reader.read()) != -1) {
                                stringBuilder.append((char) cp);
                            }

                            namemc = Boolean.parseBoolean(stringBuilder.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (namemc) {
                            if (profile.isNamemc()) {
                                player.sendMessage(ChatColor.GREEN + "You have already redeemed your NameMC rewards, thank you again for liking our server!");
                            } else {
                                profile.setNamemc(true);
                                for (Rank rank : plugin.getRankManager().getRanks().values()) {
                                    if (rank.isNameMcAward()) {
                                        profile.getRanks().add(rank);
                                    }
                                }

                                plugin.getCoreProfileManager().updatePermissions(profile);

                                player.sendMessage(ChatColor.GREEN + "Your NameMC rewards have been redeemed. Thank you for liking our server!");
                            }
                        } else {
                            profile.setNamemc(false);
                            for (Rank rank : plugin.getRankManager().getRanks().values()) {
                                if (rank.isNameMcAward()) {
                                    profile.getRanks().remove(rank);
                                }
                            }

                            StringBuilder sb = new StringBuilder();
                            sb.append("&6It looks like you have not liked our server on NameMC.");
                            sb.append("\n&aLike our server on NameMC for free rewards: namemc.com/server/" + plugin.getConfig().getString("namemc.api_server"));
                            sb.append("\n&a&oTo redeem your rewards, type /namemc again.");
                            player.sendMessage(Colors.get(sb.toString()));
                        }

                        profile.addCommandCooldown("/namemc", 10);
                    }
                });
            }
        }
        return true;
    }
}
