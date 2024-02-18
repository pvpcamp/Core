package camp.pvp.core.commands;

import camp.pvp.core.Core;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullCommand implements CommandExecutor {

    private Core plugin;
    public SkullCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("skull").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)) return true;

        if(args.length == 0) {
            player.sendMessage("Usage: /" + label + " <player>");
            return true;
        }

        ItemStack skullItem = new ItemStack(Material.SKULL_ITEM);
        skullItem.setDurability((short) 3);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setOwner(args[0]);
        skullItem.setItemMeta(skullMeta);

        player.getInventory().addItem(skullItem);

        player.sendMessage(ChatColor.GREEN + "You have received the skull of " + args[0] + ".");

        return true;
    }
}
