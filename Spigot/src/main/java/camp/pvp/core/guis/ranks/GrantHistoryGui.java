package camp.pvp.core.guis.ranks;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.events.MongoGuiEvent;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.profiles.Grant;
import camp.pvp.mongo.MongoCollectionResult;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.paginated.PaginatedGui;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class GrantHistoryGui extends PaginatedGui {

    public GrantHistoryGui(CoreProfile profile, List<Grant> grants) {
        super(profile.getName() + " Grants", 36);

        List<Grant> gList = new ArrayList<>(grants);
        Collections.sort(gList);

        for(Grant grant : gList) {
            GuiButton button = new GuiButton(Material.WOOL, grant.getType().toString());
            if(grant.getType().equals(Grant.Type.ADDED)) {
                button.setDurability((short) 4);
            } else {
                button.setDurability((short) 14);
            }

            button.setLore(
                    "&6Date: &f" + grant.getDate().toString(),
                    "&6Issued By: &f" + grant.getIssuedFromName(),
                    "&6Rank: &f" + grant.getRank().getColor() + grant.getRank().getDisplayName()
            );

            this.addButton(button, false);
        }
    }
}
