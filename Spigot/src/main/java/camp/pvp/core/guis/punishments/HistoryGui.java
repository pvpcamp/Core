package camp.pvp.core.guis.punishments;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.utils.DateUtils;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.paginated.PaginatedGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HistoryGui extends PaginatedGui {
    public HistoryGui(String title, List<Punishment> pList, boolean staff) {
        super(title, 36);

        List<Punishment> punishments = new ArrayList<>(pList);
        Collections.sort(punishments);

        int x = 0;
        for(Punishment punishment : punishments) {
            GuiButton button = new GuiButton(punishment.getType().getIcon(), punishment.getType().getColor() + punishment.getType().toString());

            if(punishment.isActive()) {
                List<String> lore = new ArrayList<>();
                lore.add("&4&lACTIVE PUNISHMENT");
                lore.add("&cPlayer: &f" + punishment.getIssuedToName());
                lore.add("&cIssued By: &f" + punishment.getIssuedFromName());
                lore.add("&cIssue Date: &f" + punishment.getIssued().toString());
                lore.add("&cReason: &f" + punishment.getReason());
                lore.add("&cExpires: &f" + (punishment.getExpires() == null ? "Never" : DateUtils.getDifference(punishment.getExpires(), new Date())));
                lore.add(" ");
                lore.add("&cIP Punishment: &f" + punishment.isIpPunished());
                lore.add("&cSilent: &f" + punishment.isSilent());

                if(staff) {
                    lore.add(" ");
                    lore.add("&aClick to rollback this punishment.");

                    button.setAction(new GuiAction() {
                        @Override
                        public void run(Player player, Gui gui) {
                            SpigotCore core = SpigotCore.getInstance();
                            core.getPunishmentManager().delete(punishment, true);

                            button.setAction(null);
                            button.setType(Material.BEDROCK);
                            button.updateName("&cPunishment Deleted");
                            button.setLore("&cThis punishment has been deleted.");
                            gui.updateGui();
                        }
                    });
                }

                button.setLore(lore);
            } else {
                if(punishment.getPardoned() != null) {
                    button.setLore(
                            "&cPlayer: &f" + punishment.getIssuedToName(),
                            "&cIssued By: &f" + punishment.getIssuedFromName(),
                            "&cIssue Date: &f" + punishment.getIssued().toString(),
                            "&cReason: &f" + punishment.getReason(),
                            " ",
                            "&cPardoned By: &f" + punishment.getPardonerName(),
                            "&cPardon Date: &f" + punishment.getPardoned().toString(),
                            "&cPardon Reason: &f" + punishment.getPardonReason(),
                            " ",
                            "&cIP Punishment: &f" + punishment.isIpPunished(),
                            "&cSilent: &f" + punishment.isSilent()
                    );
                } else {
                    button.setLore(
                            "&cPlayer: &f" + punishment.getIssuedToName(),
                            "&cIssued By: &f" + punishment.getIssuedFromName(),
                            "&cIssue Date: &f" + punishment.getIssued().toString(),
                            "&cReason: &f" + punishment.getReason(),
                            " ",
                            "&7This punishment expired.",
                            " ",
                            "&cIP Punishment: &f" + punishment.isIpPunished(),
                            "&cSilent: &f" + punishment.isSilent()
                    );
                }
            }

            button.setSlot(x);
            this.addButton(button, false);
            x++;
        }
    }
}
