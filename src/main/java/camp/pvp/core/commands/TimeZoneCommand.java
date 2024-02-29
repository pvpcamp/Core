package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.utils.Colors;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.paginated.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimeZoneCommand implements CommandExecutor {

    private Core plugin;
    public TimeZoneCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getCommand("timezone").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)) return true;

        CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfile(player.getUniqueId());

        Date date = new Date();

        List<String> sortedZones = new ArrayList<>(ZoneId.getAvailableZoneIds());
        sortedZones.sort(String::compareToIgnoreCase);

        boolean search = false;
        if(args.length > 0) {
            search = true;
            sortedZones.removeIf(zone -> !zone.toLowerCase().contains(args[0].toLowerCase()));
        }

        PaginatedGui gui = new PaginatedGui("&6Select Time Zone", 45);
        gui.setBorder(true);

        GuiButton current = new GuiButton(Material.EYE_OF_ENDER, "&6&lCurrent Time Zone");
        current.setLore("&a" + profile.getTimeZone());
        current.setSlot(search ? 3 : 4);
        gui.addNavigationButton(current);

        if(search) {
            GuiButton query = new GuiButton(Material.PAPER, "&6&lQuery");
            query.setLore("&aSearching for timezones", "&athat contain: &f" + args[0]);
            query.setSlot(5);
            gui.addNavigationButton(query);
        }

        for(String zone : sortedZones) {

            if(!zone.matches("^(Africa|America|Asia|Atlantic|Australia|Europe|Indian|Pacific)/.*")) continue;

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd YYYY hh:mm:ss a");
            sdf.setTimeZone(TimeZone.getTimeZone(zone));

            GuiButton button = new GuiButton(Material.WATCH, "&6&l" + zone);
            button.setAction((p, b, g, c) -> {
                if(profile.getTimeZone().equals(zone)) return;

                profile.setTimeZone(zone);
                p.sendMessage(Colors.get("&aYou have set your time zone to &f" + zone + "&a."));
                p.closeInventory();
            });

            button.setButtonUpdater((b, g) -> {

                List<String> lore = new ArrayList<>();
                lore.add("&6Time: &f" + sdf.format(date));
                lore.add(" ");

                if(profile.getTimeZone().equals(zone)) {
                    lore.add("&7Currently selected.");
                } else {
                    lore.add("&aClick to select.");
                }
            });

            gui.addButton(button);
        }

        gui.open(player);

        return true;
    }
}
