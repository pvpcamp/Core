package camp.pvp.core.commands;

import camp.pvp.core.Core;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

    private Core plugin;
    public FlyCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getCommand("fly").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) return true;

        Player target = (Player) sender;

        if(args.length > 0) {
            target = plugin.getServer().getPlayer(args[0]);
        }

        if(target == null) {
            sender.sendMessage("The target you specified is not on this server.");
            return true;
        }

        target.setAllowFlight(!target.getAllowFlight());
        target.setFlying(target.getAllowFlight());
        target.sendMessage(ChatColor.GREEN + "Your flight has been " + ChatColor.YELLOW + (target.getAllowFlight() ? "enabled" : "disabled") + ChatColor.GREEN + ".");

        if(target != sender) sender.sendMessage(ChatColor.GREEN + "You have " + ChatColor.YELLOW + (target.getAllowFlight() ? "enabled" : "disabled")
                + ChatColor.GREEN + " flight for " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + ".");


        return true;
    }
}
