package camp.pvp.core.commands;

import camp.pvp.command.framework.Command;
import camp.pvp.command.framework.CommandArgs;
import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.ranks.Rank;
import camp.pvp.core.utils.Colors;
import camp.pvp.core.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Random;

public class DisguiseCommand {

    private Core plugin;

    public DisguiseCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Command(name = "disguise", aliases = {"dis", "d"}, permission = "core.commands.disguise", inGameOnly = true)
    public void disguise(CommandArgs args) {
        Player player = args.getPlayer();
        CoreProfile coreProfile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if (plugin.getDisguiseManager().isDisguised(player)) {
            player.sendMessage(ChatColor.RED + "You are already disguised.");
            return;
        }

        if (coreProfile.canDisguise() || player.hasPermission("core.disguise.bypass")) {
            try {
                Rank rank = plugin.getRankManager().getDefaultRank();
                String disguise = randomName();
                plugin.getDisguiseManager().disguise(player, disguise, rank, false);
                coreProfile.addDisguiseCooldown(300);
                player.sendMessage(Colors.get("&aYou have disguised as " + rank.getColor() + disguise + "&a."));
            } catch (Exception ex) {
                player.sendMessage(ChatColor.RED + "There was an error while trying to disguise. Please try again.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.BOLD + DateUtils.getTimeUntil(coreProfile.getDisguiseCooldown()) + ChatColor.RED + " before disguising again.");
        }
    }

    public String randomName() {
        Random random = new Random();
        String adjective = plugin.getDisguiseManager().getAdjectives().get(random.nextInt(plugin.getDisguiseManager().getAdjectives().size()));
        String noun = plugin.getDisguiseManager().getNouns().get(random.nextInt(plugin.getDisguiseManager().getNouns().size()));
        int num = random.nextInt(999) + 1;
        String disguiseName = adjective + noun + (random.nextBoolean() ? num : "");
        if (!StringUtils.isAlphanumeric(disguiseName) || disguiseName.length() > 16) return randomName();
        return disguiseName;
    }
}
