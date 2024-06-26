package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommand implements CommandExecutor {

    private Core plugin;
    public ChatCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("chat").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        String senderName = (sender instanceof Player ? plugin.getCoreProfileManager().getLoadedProfile(sender.getName()).getHighestRank().getColor() + sender.getName() : "&4CONSOLE");

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {

                case "mute":
                    boolean mute = plugin.getCoreServerManager().getCoreServer().isMutedChat();
                    plugin.getCoreServerManager().getCoreServer().setMutedChat(!mute);
                    boolean updatedMute = plugin.getCoreServerManager().getCoreServer().isMutedChat();
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (updatedMute) {
                            all.sendMessage(Colors.get("&cGlobal Chat has been muted by " + senderName + "&c."));
                        } else {
                            all.sendMessage(Colors.get("&aGlobal Chat has been unmuted by " + senderName + "&a."));
                        }
                    }

                    sender.sendMessage(Colors.get("&cYou have " + (updatedMute ? "muted" : "unmuted") + " the chat."));
                    return true;

                case "slow":
                    if(args.length > 1) {
                        int time;
                        try {
                            time = Integer.parseInt(args[1]);
                            plugin.getConfig().set("chat.cooldown", time);
                            plugin.saveConfig();
                            sender.sendMessage(Colors.get("&aYou set the chat slowdown to &f" + time + "&as."));
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(ChatColor.RED + "Invalid number. Usage: /chat slow <seconds>");
                        }
                    } else {
                        int slow = plugin.getConfig().getInt("chat.cooldown");
                        sender.sendMessage(Colors.get("&aGlobal chat slowdown is currently &f" + slow + "&a."));
                    }
                    return true;
                case "clear":
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        for (int i = 0; i < 1000; i++) {
                            if (!all.hasPermission("core.staff")) {
                                all.sendMessage(" ");
                            }
                        }
                        all.sendMessage(Colors.get("&cGlobal Chat has been cleared by " + senderName + "&c."));
                    }
                    sender.sendMessage(ChatColor.GREEN + "You have cleared global chat.");
                    return true;
            }
        }

        sender.sendMessage(new String[] {
                Colors.get("&6&l/chat &r&6Help"),
                Colors.get("&6/chat mute &7- &fMute global chat"),
                Colors.get("&6/chat slow &7- &fSlow global chat"),
                Colors.get("&6/chat clear &7- &fClear global chat")
        });
        return true;
    }
}
