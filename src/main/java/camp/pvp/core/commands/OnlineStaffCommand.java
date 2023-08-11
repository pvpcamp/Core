package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.server.CoreServer;
import camp.pvp.core.server.CoreServerManager;
import camp.pvp.core.utils.Colors;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class OnlineStaffCommand {

    private Core plugin;

    public OnlineStaffCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Command(name = "onlinestaff", permission = "core.staff")
    public void onlineStaff(CommandArgs args) {

        CoreServerManager csm = plugin.getCoreServerManager();
        StringBuilder onlineStaff = new StringBuilder();

        onlineStaff.append(Colors.get("&6&lOnline Staff: "));
        for (CoreServer coreServer : csm.getCoreServers()) {
            if (!coreServer.getStaffList().equalsIgnoreCase("N/A")) {
                List<String> staff = Arrays.asList(coreServer.getStaffList().split(","));
                StringBuilder list = new StringBuilder();
                list.append(Colors.get("\n &6" + coreServer.getName() + ": "));
                staff.forEach(r -> {
                    CoreProfile coreProfile = plugin.getCoreProfileManager().find(r, false);
                    list.append(Colors.get(coreProfile.getHighestRank().getColor() + coreProfile.getName() + "&7, "));
                });
                onlineStaff.append(list.substring(0, list.length() - 2));
            }
        }
        if (onlineStaff.toString().contains("\n")) {
            args.getSender().sendMessage(onlineStaff.toString());
        } else {
            args.getSender().sendMessage(ChatColor.RED + "There are currently no online staff members.");
        }
    }
}
