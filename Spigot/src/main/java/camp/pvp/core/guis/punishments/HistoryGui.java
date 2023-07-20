package camp.pvp.core.guis.punishments;

import camp.pvp.core.punishments.Punishment;
import camp.pvp.core.utils.DateUtils;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.paginated.PaginatedGui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HistoryGui extends PaginatedGui {
    public HistoryGui(String title, List<Punishment> pList) {
        super(title, 36);

        List<Punishment> punishments = new ArrayList<>(pList);
        Collections.sort(punishments);

        int x = 0;
        for(Punishment punishment : punishments) {
            GuiButton button = new GuiButton(punishment.getType().getIcon(), punishment.getType().getColor() + punishment.getType().toString());

            if(punishment.isActive()) {
                button.setLore(
                        "&4&lACTIVE PUNISHMENT",
                        "&cPlayer: &f" + punishment.getIssuedToName(),
                        "&cIssued By: &f" + punishment.getIssuedFromName(),
                        "&cIssue Date: &f" + punishment.getIssued().toString(),
                        "&cReason: &f" + punishment.getReason(),
                        "&cExpires: &f" + (punishment.getExpires() == null ? "Never" : DateUtils.getDifference(punishment.getExpires(), new Date())),
                        " ",
                        "&cIP Punishment: &f" + punishment.isIpPunished(),
                        "&cSilent: &f" + punishment.isSilent()
                );
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
