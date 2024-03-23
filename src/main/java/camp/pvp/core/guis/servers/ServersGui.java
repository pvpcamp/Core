package camp.pvp.core.guis.servers;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.server.CoreServer;
import camp.pvp.core.server.CoreServerManager;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.StandardGui;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;

public class ServersGui extends StandardGui {

    private CoreProfile profile;
    private CoreServerManager csm;

    public ServersGui(CoreProfile profile) {
        super("&6&lPvP Camp Network", 45);
        this.profile = profile;
        this.csm = Core.getInstance().getCoreServerManager();
    }

    @Override
    public void updateGui() {

        List<GuiButton> buttons = new ArrayList<>();

        for(CoreServer server : csm.getCoreServers()) {

            if(!server.isCurrentlyOnline()) continue;
            if(!server.isShowInServerList()) continue;
            if(server.isStaffOnlyServerList() && !profile.getPlayer().hasPermission("core.staff")) continue;

            GuiButton button = new GuiButton(server.getMaterial(), "&6&l" + server.getDisplayName());
            List<String> lore = new ArrayList<>();

            lore.add(" ");
            lore.addAll(server.getMotd());
            lore.add(" ");
            lore.add("&6Players: &f" + server.getPlayers().size() + "/" + server.getSlots());
            lore.add(" ");

            if(csm.getCoreServer().getName().equals(server.getName())) {
                lore.add("&cYou are currently on this server.");
            } else {
                if(server.isWhitelisted()) {
                    lore.add("&cThis server is currently whitelisted.");
                } else {
                    lore.add("&aClick to join this server.");
                }

                button.setAction((p, b, g, clickType) -> {

                    p.sendMessage(ChatColor.GREEN + "Sending you to " + ChatColor.WHITE + server.getDisplayName() + ChatColor.GREEN + "...");

                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Connect");
                    out.writeUTF(server.getName());

                    p.sendPluginMessage(Core.getInstance(), "BungeeCord", out.toByteArray());
                });
            }

            button.setLore(lore);
            button.setSlot(server.getServerSlot());
            buttons.add(button);
        }

        setButtons(buttons);

        super.updateGui();
    }
}
