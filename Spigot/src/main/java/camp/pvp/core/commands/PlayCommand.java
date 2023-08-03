package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.SpigotCore;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayCommand {

    private SpigotCore plugin;
    public PlayCommand(SpigotCore plugin) {
        this.plugin = plugin;
    }

    @Command(name = "play",
            aliases = "connect",
            permission = "core.commands.play",
            description = "Connect to a remote server.",
            inGameOnly = true)
    public void play(CommandArgs commandArgs) {
        String[] args = commandArgs.getArgs();
        Player player = commandArgs.getPlayer();

        if(args.length > 0) {

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(args[0]);

            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /" + commandArgs.getLabel() + " <server>");
        }
    }
}
