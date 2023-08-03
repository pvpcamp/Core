package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.SpigotCore;

public class StaffRollbackCommand {

    private SpigotCore plugin;
    public StaffRollbackCommand(SpigotCore plugin) {
        this.plugin = plugin;
    }

    @Command(name = "staffrollback",
            permission = "core.commands.staffrollback")
    public void staffRollback(CommandArgs commandArgs) {

    }
}
