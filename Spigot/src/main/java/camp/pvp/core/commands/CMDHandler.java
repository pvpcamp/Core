package camp.pvp.core.commands;


import camp.pvp.NetworkHelper;
import camp.pvp.command.CommandHandler;
import camp.pvp.core.commands.impl.*;

public class CMDHandler {

    public CMDHandler() {
        CommandHandler commandHandler = NetworkHelper.getInstance().getCommandHandler();
        commandHandler.registerCommand(new AltsCommand());
        commandHandler.registerCommand(new FeedCommand());
        commandHandler.registerCommand(new FlyCommand());
        commandHandler.registerCommand(new GamemodeCommand());
        commandHandler.registerCommand(new HealCommand());
        commandHandler.registerCommand(new PlayerLookupCommand());
        commandHandler.registerCommand(new PlaytimeCommand());
        commandHandler.registerCommand(new SeenCommand());
    }
}