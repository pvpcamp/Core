package camp.pvp.core.guis.tags;

import camp.pvp.core.Core;
import camp.pvp.core.chattags.ChatTag;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.utils.Colors;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.paginated.PaginatedGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChooseTagGui extends PaginatedGui {

    public ChooseTagGui(CoreProfile profile) {
        super("&6Choose a Tag", 45);

        setBorder(true);

        List<ChatTag> tags = new ArrayList<>(Core.getInstance().getChatTagManager().getChatTags().values());
        Collections.sort(tags);

        Rank rank = profile.getHighestRank();
        String prefix = rank.getPrefix();

        for (ChatTag tag : tags) {
            GuiButton button = new GuiButton(Material.NAME_TAG, "&f" + tag.getDisplayName());

            List<String> lines = new ArrayList<>();
            lines.add("&6Tag: &f" + tag.getTag());

            lines.add("&6Preview: &f" + (prefix == null ? "" : prefix + " ") + rank.getColor() + profile.getName() + " " + tag.getTag());
            lines.add(" ");

            boolean show = tag.equals(profile.getChatTag())
                    || tag.isVisible()
                    || profile.getOwnedChatTags().contains(tag)
                    || profile.getPlayer().hasPermission("core.tags.all");

            if(profile.getChatTag() != null && profile.getChatTag().equals(tag)) {
                button.updateName(tag.getDisplayName() + " &7(Selected)");
                button.setAction(new GuiAction() {
                    @Override
                    public void run(Player player, GuiButton guiButton, Gui gui, ClickType clickType) {
                        profile.setChatTag(null);
                        player.sendMessage(ChatColor.GREEN + "You no longer have a chat tag applied.");
                    }
                });

                button.setCloseOnClick(true);

                lines.add("&7Click to remove this tag.");
            } else {
                if(profile.getOwnedChatTags().contains(tag) || profile.getPlayer().hasPermission("core.tags.all")) {
                    button.setAction(new GuiAction() {
                        @Override
                        public void run(Player player, GuiButton guiButton, Gui gui, ClickType clickType) {
                            profile.setChatTag(tag);
                            player.sendMessage(Colors.get("&aYou applied have the chat tag &f" + tag.getDisplayName() + "&a."));
                        }
                    });

                    lines.add("&aClick to apply this tag.");
                    button.setCloseOnClick(true);
                } else {
                    button.setType(Material.PAPER);
                    lines.add("&cYou do not own this tag.");
                    lines.add("&eYou can purchase tags here:");
                    lines.add("&fstore.pvp.camp");
                }
            }

            button.setLore(lines);

            if(show) {
                this.addButton(button, false);
            }
        }
    }
}
