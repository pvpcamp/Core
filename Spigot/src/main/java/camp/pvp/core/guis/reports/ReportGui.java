package camp.pvp.core.guis.reports;

import camp.pvp.core.SpigotCore;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.server.CoreServerManager;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ReportGui extends StandardGui {
    public ReportGui(CoreProfile profile, String target) {
        super("&cReport " + target, 27);

        this.setDefaultBackground();

        GuiButton combatHacks = new GuiButton(Material.DIAMOND_SWORD, "&6Combat Hacks");
        combatHacks.setLore(
                "&aKillAura, Reach, Velocity, etc.",
                "&7Click to report &c" + target + "&7."
        );

        combatHacks.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                CoreServerManager csm = SpigotCore.getInstance().getCoreServerManager();
                csm.sendStaffMessage("&c[Report] &f" + profile.getName() + " &chas reported &f" + target + "&c on server &f" + csm.getCoreServer().getName() + "&c for: &fCombat Hacks");
                player.sendMessage(ChatColor.GREEN + "Your report of " + ChatColor.WHITE + target + ChatColor.GREEN + " has been submitted and sent to online staff.");

                profile.addCommandCooldown("/report", 30);
            }
        });

        combatHacks.setCloseOnClick(true);
        combatHacks.setSlot(10);
        this.addButton(combatHacks, false);

        GuiButton campingHiding = new GuiButton(Material.SAPLING, "&6Camping or Hiding");
        campingHiding.setLore(
                "&aCamping or hiding from you during game.",
                "&7Click to report &c" + target + "&7."
        );

        campingHiding.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                CoreServerManager csm = SpigotCore.getInstance().getCoreServerManager();
                csm.sendStaffMessage("&c[Report] &f" + profile.getName() + " &chas reported &f" + target + "&c on server &f" + csm.getCoreServer().getName() + "&c for: &fCamping or Hiding");
                player.sendMessage(ChatColor.GREEN + "Your report of " + ChatColor.WHITE + target + ChatColor.GREEN + " has been submitted and sent to online staff.");

                profile.addCommandCooldown("/report", 30);
            }
        });

        campingHiding.setCloseOnClick(true);
        campingHiding.setSlot(11);
        this.addButton(campingHiding, false);

        GuiButton delayingFightEvent = new GuiButton(Material.WEB, "&6Delaying Fight or Event");
        delayingFightEvent.setLore(
                "&aPurposely delaying a fight",
                "&aor event as long as possible.",
                "&7Click to report &c" + target + "&7."
        );

        delayingFightEvent.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                CoreServerManager csm = SpigotCore.getInstance().getCoreServerManager();
                csm.sendStaffMessage("&c[Report] &f" + profile.getName() + " &chas reported &f" + target + "&c on server &f" + csm.getCoreServer().getName() + "&c for: &fDelaying Fight or Event");
                player.sendMessage(ChatColor.GREEN + "Your report of " + ChatColor.WHITE + target + ChatColor.GREEN + " has been submitted and sent to online staff.");

                profile.addCommandCooldown("/report", 30);
            }
        });

        delayingFightEvent.setCloseOnClick(true);
        delayingFightEvent.setSlot(12);
        this.addButton(delayingFightEvent, false);

        GuiButton chat = new GuiButton(Material.PAPER, "&6Chat");
        chat.setLore(
                "&aInappropriate messages in global",
                "&achat or through private messages.",
                "&7Click to report &c" + target + "&7."
        );

        chat.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                CoreServerManager csm = SpigotCore.getInstance().getCoreServerManager();
                csm.sendStaffMessage("&c[Report] &f" + profile.getName() + " &chas reported &f" + target + "&c on server &f" + csm.getCoreServer().getName() + "&c for: &fDelaying Fight or Event");
                player.sendMessage(ChatColor.GREEN + "Your report of " + ChatColor.WHITE + target + ChatColor.GREEN + " has been submitted and sent to online staff.");

                profile.addCommandCooldown("/report", 30);
            }
        });

        chat.setCloseOnClick(true);
        chat.setSlot(13);
        this.addButton(chat, false);
    }
}
