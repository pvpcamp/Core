package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FlyCommand {

    @Command(name = "fly", description = "Toggle a players flight.", permission = "core.commands.fly", inGameOnly = true)
    public void fly(CommandArgs args) {

        Player player = args.getPlayer();

        if (args.length() == 0) {
            player.setAllowFlight(!player.getAllowFlight());
            player.sendMessage(Colors.get("&6Your flight has been " + (player.getAllowFlight() ? "&aenabled" : "&cdisabled") + "&6."));
            return;
        }

        String name = args.getArgs(0);

        if (Bukkit.getPlayer(name) != null) {
            Player target = Bukkit.getPlayer(name);
            target.setAllowFlight(!target.getAllowFlight());
            target.sendMessage(Colors.get("&6Your flight has been " + (target.getAllowFlight() ? "&aenabled" : "&cdisabled" + "&6.")));
            player.sendMessage(Colors.get("&6You have set &f" + target.getName() + "&6's flight to " + (target.getAllowFlight() ? "&aenabled" : "&cdisabled") + "&6."));
        } else {
            player.sendMessage(ChatColor.RED + name + " is not online.");
        }
    }
}
