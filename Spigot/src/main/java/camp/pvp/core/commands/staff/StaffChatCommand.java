package camp.pvp.core.commands.staff;

import camp.pvp.core.SpigotCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StaffChatCommand implements CommandExecutor {

    private SpigotCore plugin;
    public StaffChatCommand(SpigotCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}
