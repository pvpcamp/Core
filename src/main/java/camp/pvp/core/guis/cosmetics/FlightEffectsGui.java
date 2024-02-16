package camp.pvp.core.guis.cosmetics;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.profiles.FlightEffect;
import camp.pvp.core.utils.Colors;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import org.bukkit.entity.Player;

public class FlightEffectsGui extends ArrangedGui {

    private static final Core plugin = Core.getInstance();

    public FlightEffectsGui(Player player) {
        super("&6Flight Effects");

        this.setDefaultBorder();

        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfile(player.getUniqueId());

        for(FlightEffect effect : FlightEffect.values()) {
            boolean owned = effect.equals(FlightEffect.NONE) || profile.getOwnedFlightEffects().contains(effect) || player.hasPermission("core.cosmetics.flight_effects.all");

            GuiButton button = new GuiButton(effect.getMaterial(), "&6&l" + effect.toString());

            if(profile.getAppliedFlightEffect().equals(effect)) {
                button.addGlowing();
            }

            button.setButtonUpdater((b, g) -> {
                if(profile.getAppliedFlightEffect().equals(effect)) {
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
                            "&cto this flight effect.",
                            " ",
                            "&aYou can purchase cosmetics here:",
                            "&fstore.pvp.camp"
                    );
                }
            });

            button.setAction((p, b, g, click) -> {

                if(profile.getAppliedFlightEffect().equals(effect)) {
                    return;
                }

                if(owned) {
                    profile.setAppliedFlightEffect(effect);
                    p.sendMessage(Colors.get("&aYou have applied the &f" + effect.toString() + " &aflight effect."));
                    g.updateGui();
                }
            });

            addButton(button);
        }
    }
}
