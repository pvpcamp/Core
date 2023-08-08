package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.Core;

public class StaffRollbackCommand {

    private Core plugin;
    public StaffRollbackCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Command(name = "staffrollback",
            permission = "core.commands.staffrollback")
    public void staffRollback(CommandArgs commandArgs) {

    }
}
