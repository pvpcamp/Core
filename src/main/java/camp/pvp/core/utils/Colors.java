package camp.pvp.core.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Colors {
    public static String get(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String strip(String s) {
        return ChatColor.stripColor(get(s));
    }

    public static ItemStack convertToItem(ChatColor color) {
        switch(color) {
            case BLACK:
                return new ItemStack(Material.INK_SACK, 1, (short) 0);
            case DARK_BLUE:
                return new ItemStack(Material.INK_SACK, 1, (short) 4);
            case DARK_GREEN:
                return new ItemStack(Material.INK_SACK, 1, (short) 2);
            case DARK_AQUA:
                return new ItemStack(Material.INK_SACK, 1, (short) 6);
            case DARK_RED:
                return new ItemStack(Material.REDSTONE);
            case DARK_PURPLE:
                return new ItemStack(Material.INK_SACK, 1, (short) 5);
            case GOLD:
                return new ItemStack(Material.INK_SACK, 1, (short) 14);
            case GRAY:
                return new ItemStack(Material.INK_SACK, 1, (short) 7);
            case DARK_GRAY:
                return new ItemStack(Material.INK_SACK, 1, (short) 8);
            case BLUE:
                return new ItemStack(Material.INK_SACK, 1, (short) 12);
            case GREEN:
                return new ItemStack(Material.INK_SACK, 1, (short) 10);
            case AQUA:
                return new ItemStack(Material.INK_SACK, 1, (short) 9);
            case RED:
                return new ItemStack(Material.INK_SACK, 1, (short) 1);
            case LIGHT_PURPLE:
                return new ItemStack(Material.INK_SACK, 1, (short) 13);
            case YELLOW:
                return new ItemStack(Material.INK_SACK, 1, (short) 11);
            case WHITE:
                return new ItemStack(Material.INK_SACK, 1, (short) 15);
            default:
                return new ItemStack(Material.GLASS);
        }
    }
}
