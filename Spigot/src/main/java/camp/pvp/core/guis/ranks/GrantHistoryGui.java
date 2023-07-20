package camp.pvp.core.guis.ranks;

import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.profiles.Grant;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.paginated.PaginatedGui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GrantHistoryGui extends PaginatedGui {

    public GrantHistoryGui(CoreProfile profile, List<Grant> grants) {
        super(profile.getName() + " Grants", 36);

        List<Grant> gList = new ArrayList<>(grants);
        Collections.sort(gList);

        for(Grant grant : gList) {
            GuiButton button = new GuiButton(grant.getType().getIcon(), "&6" + grant.getType().toString());
            button.setLore(
                    "&6Date: &f" + grant.getDate().toString(),
                    "&6Issued By: &f" + grant.getIssuedFromName(),
                    "&6Rank: &f" + grant.getRank().getColor() + grant.getRank().getDisplayName()
            );

            this.addButton(button, false);
        }
    }
}
