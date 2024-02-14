package camp.pvp.core.profiles;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum LobbyArmor {
    NONE, ASTRONAUT, GOLD;

    public ItemStack[] getArmor() {
        ItemStack[] armor = new ItemStack[4];
        switch(this) {
            case ASTRONAUT -> {
                armor[3] = new ItemStack(Material.GLASS);
                armor[2] = new ItemStack(Material.IRON_CHESTPLATE);
                armor[1] = new ItemStack(Material.IRON_LEGGINGS);
                armor[0] = new ItemStack(Material.IRON_BOOTS);
            }
            case GOLD -> {
                armor[3] = new ItemStack(Material.GOLD_HELMET);
                armor[2] = new ItemStack(Material.GOLD_CHESTPLATE);
                armor[1] = new ItemStack(Material.GOLD_LEGGINGS);
                armor[0] = new ItemStack(Material.GOLD_BOOTS);
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
