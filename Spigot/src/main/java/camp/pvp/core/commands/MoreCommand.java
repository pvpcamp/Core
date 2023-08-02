package camp.pvp.core.commands;

import camp.pvp.core.SpigotCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MoreCommand implements CommandExecutor {

    private SpigotCore plugin;
    public MoreCommand(SpigotCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("more").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack item = player.getItemInHand();

            if(item != null && !item.getType().equals(Material.AIR)) {
                item.setAmount(64);
                player.sendMessage(ChatColor.GREEN + "There you go.");
            } else {
                player.sendMessage(ChatColor.RED + "What are you trying to get more of?");
            }
        }

        return true;
    }
}
