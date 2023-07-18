package camp.pvp.core.guis;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.ranks.RankManager;
import camp.pvp.core.utils.Colors;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GrantGui extends StandardGui {
    public GrantGui(SpigotCore plugin, CoreProfile profile, CoreProfile target) {
        super("&6Grant " + target.getName(), 27);

        RankManager rankManager = plugin.getRankManager();
        List<Rank> ranks = new ArrayList<>(rankManager.getRanks().values());
        Collections.sort(ranks);

        int slot = 0;
        for(Rank rank : ranks) {
            GuiButton button = new GuiButton(Material.INK_SACK, rank.getColor() + rank.getDisplayName());
            button.setButtonUpdater(new AbstractButtonUpdater() {
                @Override
                public void update(GuiButton guiButton, Gui gui) {
                    if(rank.getWeight() < profile.getHighestRank().getWeight() || profile.getPlayer().hasPermission("*")) {
                        guiButton.setLore(
                                "&6Preview: &f" + (rank.getPrefix() == null ? "" : rank.getPrefix() + " ") + rank.getColor() + target.getName(),
                                "&6Weight: &f" + rank.getWeight(),
                                "&7Click to apply this rank to " + target.getName() + "."
                        );

                        if (target.getRanks().contains(rank)) {
                            button.setDurability((short) 10);
                            button.updateName(rank.getColor() + rank.getDisplayName() + " &7(Applied)");
                        } else {
                            button.updateName(rank.getColor() + rank.getDisplayName());
                            button.setDurability((short) 8);
                        }
                    } else {
                        guiButton.setLore(
                                "&cNo permission."
                        );
                    }
                }
            });

            if(rank.getWeight() < profile.getHighestRank().getWeight() || profile.getPlayer().hasPermission("*")) {
                button.setAction(new GuiAction() {
                    @Override
                    public void run(Player player, Gui gui) {
                        String message;
                        if(target.getRanks().contains(rank)) {
                            target.getRanks().remove(rank);
                            message = "&aYou no longer have the rank " + rank.getColor() + rank.getDisplayName() + "&a.";
                        } else {
                            target.getRanks().add(rank);
                            message = "&aYou have been granted the rank " + rank.getColor() + rank.getDisplayName() + "&a.";
                        }

                        if(target.getPlayer() != null) {
                            target.getPlayer().sendMessage(Colors.get(message));
                        }

                        plugin.getCoreProfileManager().updatePermissions(target);
                        gui.updateGui();
                    }
                });
            }

            button.setSlot(slot);
            this.addButton(button, false);

            slot++;
        }
    }
}
