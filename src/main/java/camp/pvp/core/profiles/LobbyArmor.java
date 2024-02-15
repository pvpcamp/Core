package camp.pvp.core.profiles;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public enum LobbyArmor {
    NONE, ASTRONAUT, DIAMOND, GOLD, LEATHER_BLACK, LEATHER_BLUE, LEATHER_RED, LEATHER_YELLOW, LEATHER_WHITE;

    public ItemStack[] getArmor() {
        ItemStack[] armor = new ItemStack[4];
        switch(this) {
            case ASTRONAUT -> {
                armor[3] = new ItemStack(Material.GLASS);
                armor[2] = new ItemStack(Material.IRON_CHESTPLATE);
                armor[1] = new ItemStack(Material.IRON_LEGGINGS);
                armor[0] = new ItemStack(Material.IRON_BOOTS);
            }
            case DIAMOND -> {
                armor[3] = new ItemStack(Material.DIAMOND_HELMET);
                armor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
                armor[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
                armor[0] = new ItemStack(Material.DIAMOND_BOOTS);
            }
            case GOLD -> {
                armor[3] = new ItemStack(Material.GOLD_HELMET);
                armor[2] = new ItemStack(Material.GOLD_CHESTPLATE);
                armor[1] = new ItemStack(Material.GOLD_LEGGINGS);
                armor[0] = new ItemStack(Material.GOLD_BOOTS);
            }
            case NONE -> {
            }
            default -> {
                armor[3] = new ItemStack(Material.LEATHER_HELMET);
                armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
                armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);
                armor[0] = new ItemStack(Material.LEATHER_BOOTS);

                LeatherArmorMeta helmetMeta = (LeatherArmorMeta) armor[3].getItemMeta();
                LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) armor[2].getItemMeta();
                LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) armor[1].getItemMeta();
                LeatherArmorMeta bootsMeta = (LeatherArmorMeta) armor[0].getItemMeta();

                switch(this) {
                    case LEATHER_BLACK -> {
                        helmetMeta.setColor(Color.BLACK);
                        chestplateMeta.setColor(Color.BLACK);
                        leggingsMeta.setColor(Color.BLACK);
                        bootsMeta.setColor(Color.BLACK);
                    }
                    case LEATHER_BLUE -> {
                        helmetMeta.setColor(Color.BLUE);
                        chestplateMeta.setColor(Color.BLUE);
                        leggingsMeta.setColor(Color.BLUE);
                        bootsMeta.setColor(Color.BLUE);
                    }
                    case LEATHER_RED -> {
                        helmetMeta.setColor(Color.RED);
                        chestplateMeta.setColor(Color.RED);
                        leggingsMeta.setColor(Color.RED);
                        bootsMeta.setColor(Color.RED);
                    }
                    case LEATHER_YELLOW -> {
                        helmetMeta.setColor(Color.YELLOW);
                        chestplateMeta.setColor(Color.YELLOW);
                        leggingsMeta.setColor(Color.YELLOW);
                        bootsMeta.setColor(Color.YELLOW);
                    }
                    case LEATHER_WHITE -> {
                        helmetMeta.setColor(Color.WHITE);
                        chestplateMeta.setColor(Color.WHITE);
                        leggingsMeta.setColor(Color.WHITE);
                        bootsMeta.setColor(Color.WHITE);
                    }
                }

                armor[3].setItemMeta(helmetMeta);
                armor[2].setItemMeta(chestplateMeta);
                armor[1].setItemMeta(leggingsMeta);
                armor[0].setItemMeta(bootsMeta);
            }
        }

        return armor;
    }

    @Override
    public String toString() {
        String name = this.name();
        name = name.replace("_", " ");
        return WordUtils.capitalizeFully(name);
    }
}
