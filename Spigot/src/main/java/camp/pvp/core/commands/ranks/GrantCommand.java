package camp.pvp.core.commands.ranks;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.guis.ranks.GrantGui;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantCommand implements CommandExecutor {

    private SpigotCore plugin;
    public GrantCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("grant").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length > 0) {
            int weight = Integer.MAX_VALUE;
            if(sender instanceof Player) {
                Player player = (Player) sender;
                CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
                weight = profile.getHighestRank().getWeight();
            }

            switch(args.length) {
                case 1:
                    if(sender instanceof Player) {
                        String name = args[0];
                        Player player = (Player) sender;
                        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
                        CoreProfile target = plugin.getCoreProfileManager().find(name, false);
                        if (target != null) {
                            new GrantGui(plugin, profile, target).open(player);
                        } else {
                            sender.sendMessage(ChatColor.RED + "The player you specified does not have a profile on the network.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You must be a player to open the grant GUI.");
                    }
                    return true;
                case 2:
            }
        }

        StringBuilder help = new StringBuilder();
        help.append("&6&l/grant &r&6Help");
        help.append("\n&6/grant <player> &7- &fOpens the grant GUI.");
        help.append("\n&6/grant add <player> <rank> &7- &fGrant a rank to a player.");
        help.append("\n&6/grant remove <player> <rank> &7- &fRemoves a rank from a player.");
        help.append("\n&6/grant list <player> &7- &fView the ranks a player has.");

        sender.sendMessage(Colors.get(help.toString()));

        return true;
    }
}
