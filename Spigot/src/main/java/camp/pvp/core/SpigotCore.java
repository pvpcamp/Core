package camp.pvp.core;

import camp.pvp.NetworkHelper;
import camp.pvp.core.commands.personalization.SoundsCommand;
import camp.pvp.core.commands.personalization.ToggleGlobalChatCommand;
import camp.pvp.core.commands.personalization.ToggleMessagesCommand;
import camp.pvp.core.commands.punishments.*;
import camp.pvp.core.commands.ranks.GrantHistoryCommand;
import camp.pvp.core.commands.ranks.GrantsCommand;
import camp.pvp.core.commands.ranks.RankCommand;
import camp.pvp.core.commands.staff.StaffModeCommand;
import camp.pvp.core.commands.users.IgnoreCommand;
import camp.pvp.core.commands.users.MessageCommand;
import camp.pvp.core.commands.users.UnignoreCommand;
import camp.pvp.core.listeners.mongo.MongoGuiListener;
import camp.pvp.core.listeners.pearls.PlayerTeleportListener;
import camp.pvp.core.listeners.player.PlayerChatListener;
import camp.pvp.core.listeners.player.PlayerJoinLeaveListeners;
import camp.pvp.core.profiles.CoreProfileManager;
import camp.pvp.core.punishments.PunishmentManager;
import camp.pvp.core.ranks.RankManager;
import camp.pvp.core.chattags.ChatTagManager;
import camp.pvp.core.server.CoreServer;
import camp.pvp.mongo.MongoManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotCore extends JavaPlugin {

    private @Getter static SpigotCore instance;

    private @Getter CoreServer coreServer;

    private @Getter MongoManager mongoManager;

    private @Getter ChatTagManager chatTagManager;
    private @Getter CoreProfileManager coreProfileManager;
    private @Getter PunishmentManager punishmentManager;
    private @Getter RankManager rankManager;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        this.coreServer = new CoreServer(getConfig().getString("server.name"), getConfig().getString("server.type"));

        this.mongoManager = NetworkHelper.getInstance().getMongoManager();

        this.chatTagManager = new ChatTagManager(this);
        this.rankManager = new RankManager(this);
        this.punishmentManager = new PunishmentManager(this);
        this.coreProfileManager = new CoreProfileManager(this);

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        coreProfileManager.shutdown();

        instance = null;
    }

    public void registerCommands() {
        // Personalization
        new SoundsCommand(this);
        new ToggleGlobalChatCommand(this);
        new ToggleMessagesCommand(this);

        // Punishments
        new BanCommand(this);
        new BlacklistCommand(this);
        new HistoryCommand(this);
        new KickCommand(this);
        new MuteCommand(this);
        new StaffHistoryCommand(this);
        new TempBanCommand(this);
        new TempMuteCommand(this);
        new UnbanCommand(this);
        new UnblacklistCommand(this);
        new UnmuteCommand(this);

        // Ranks
        new GrantsCommand(this);
        new GrantHistoryCommand(this);
        new RankCommand(this);

        // Communication
        new IgnoreCommand(this);
        new MessageCommand(this);
        new UnignoreCommand(this);

        // Staff
        new StaffModeCommand(this);
    }

    public void registerListeners() {
        new PlayerChatListener(this);
        new PlayerJoinLeaveListeners(this);
        new PlayerTeleportListener(this);

        new MongoGuiListener(this);
    }
}