package camp.pvp.core.guis.cosmetics;

import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ColorGui extends ArrangedGui {

    public ColorGui(CoreProfile profile) {
        super("&6Change Name Color");

        this.setDefaultBorder();

        GuiButton none = new GuiButton(Material.PAPER, "&6&lNone");
        none.setAction((player, guiButton, gui, clickType) -> {
            profile.setChatColor(null);
        });

        none.setButtonUpdater((guiButton, gui) -> {
            if(profile.getChatColor() == null) {
                guiButton.setLore("&7&oApplied.");
                guiButton.addGlowing();
            } else {
                guiButton.setLore("&aClick to use your rank color.");
                guiButton.removeGlowing();
            }
        });

        addButton(none);

        for(ChatColor color : ChatColor.values()) {
            if(!color.isColor()) continue;

            GuiButton button = new GuiButton(Colors.convertToItem(color), color + color.name());

            button.setAction((player, guiButton, gui, clickType) -> {
                profile.setChatColor(color);
            });

            button.setButtonUpdater((guiButton, gui) -> {
                if(profile.getChatColor() == color) {
                    guiButton.setLore("&7&oApplied.");
                    guiButton.addGlowing();
                } else {
                    guiButton.setLore("&aClick to use this color.");
                    guiButton.removeGlowing();
                }
            });
        }
    }
}
