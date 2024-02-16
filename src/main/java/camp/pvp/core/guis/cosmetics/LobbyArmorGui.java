package camp.pvp.core.guis.cosmetics;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.profiles.LobbyArmor;
import camp.pvp.core.utils.Colors;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LobbyArmorGui extends ArrangedGui {

    private static final Core plugin = Core.getInstance();

    public LobbyArmorGui(Player player) {
        super("&6Lobby Armor");

        this.setDefaultBorder();

        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfile(player.getUniqueId());

        for(LobbyArmor armor : LobbyArmor.values()) {
            boolean owned = armor.equals(LobbyArmor.NONE) || profile.getOwnedLobbyArmor().contains(armor) || player.hasPermission("core.cosmetics.lobby_armor.all");

            GuiButton button = new GuiButton(armor.equals(LobbyArmor.NONE) ? new ItemStack(Material.CHAINMAIL_CHESTPLATE) : armor.getArmor()[2], "&6&l" + armor.toString());

            if(profile.getAppliedLobbyArmor().equals(armor)) {
                button.addGlowing();
            }

            button.setButtonUpdater((b, g) -> {
                if(profile.getAppliedLobbyArmor().equals(armor)) {
                    b.addGlowing();
                    b.setLore("&7Currently applied.");
                    return;
                }

                b.removeGlowing();

                if(owned) {
                    b.setLore("&aClick to apply.");
                } else {
                    b.setLore(
                            "&cYou do not have access",
                            "&cto this lobby armor.",
                            " ",
                            "&aYou can purchase cosmetics here:",
                            "&fstore.pvp.camp"
                    );
                }
            });

            button.setAction((p, b, g, click) -> {

                if(profile.getAppliedLobbyArmor().equals(armor)) {
                    return;
                }

                if(owned) {
                    profile.setAppliedLobbyArmor(armor);
                    p.sendMessage(Colors.get("&aYou have applied the &f" + armor.toString() + " &alobby armor."));
                    g.updateGui();
                }
            });

            addButton(button);
        }
    }
}
