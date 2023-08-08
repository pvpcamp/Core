package camp.pvp.core.commands;

import camp.pvp.core.Core;
import camp.pvp.core.guis.tags.ChooseTagGui;
import camp.pvp.core.profiles.CoreProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TagsCommand implements CommandExecutor {

    private Core plugin;
    public TagsCommand(Core plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("tags").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            CoreProfile profile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
            if(profile != null) {
                new ChooseTagGui(profile).open(player);
            }
        }

        return true;
    }
}
