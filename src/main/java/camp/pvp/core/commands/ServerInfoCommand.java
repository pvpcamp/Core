package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.MiniProfile;
import camp.pvp.core.server.CoreServer;
import camp.pvp.core.utils.Colors;
import camp.pvp.core.utils.DateUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServerInfoCommand implements CommandExecutor {

    private Core plugin;
    public ServerInfoCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getCommand("serverinfo").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        CoreServer server = args.length > 0 ? plugin.getCoreServerManager().findServer(args[0]) : plugin.getCoreServerManager().getCoreServer();
        if(server == null) {
            sender.sendMessage(ChatColor.RED + "Server not found.");
            return true;
        }

        Date startTime = new Date();
        startTime.setTime(server.getUpTime());

        StringBuilder sb = new StringBuilder();
        sb.append("&6Server: &f" + server.getName());
        sb.append("\n&6Type: &f" + server.getType());

        if(server.isCurrentlyOnline()) {
            sb.append("\n&6Uptime: &f" + DateUtils.getDifference(new Date(), startTime));
            sb.append("\n&6Players: &f" + server.getPlayers().size() + "/" + server.getSlots());
        }

        List<MiniProfile> staff = new ArrayList<>();
        server.getPlayers().forEach(p -> {
            if(p.isStaff()) {
                staff.add(p);
            }
        });

        if(!staff.isEmpty()) {
            sb.append("\n&6Staff: &f");
            for(int i = 0; i < staff.size(); i++) {
                MiniProfile profile = staff.get(i);
                sb.append(profile.getName() + (i + 1 == staff.size() ? "" : "&7, &f"));
            }
        }

        sb.append(server.isCurrentlyOnline() ? "\n&eThis server is online." : "\n&cThis server is currently offline.");

        sender.sendMessage(Colors.get(sb.toString()));

        return true;
    }
}
