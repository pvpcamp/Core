package camp.pvp.core;

import camp.pvp.core.commands.*;
import camp.pvp.core.commands.GrantCommand;
import camp.pvp.core.commands.GrantHistoryCommand;
import camp.pvp.core.commands.GrantsCommand;
import camp.pvp.core.commands.RankCommand;
import camp.pvp.core.commands.StaffChatCommand;
import camp.pvp.core.commands.TagManagerCommand;
import camp.pvp.core.commands.TagsCommand;
import camp.pvp.core.commands.DemoCommand;
import camp.pvp.core.listeners.mongo.MongoGuiListener;
import camp.pvp.core.listeners.pearls.PlayerTeleportListener;
import camp.pvp.core.listeners.player.PlayerChatListener;
import camp.pvp.core.listeners.player.PlayerCommandPreprocessListener;
import camp.pvp.core.listeners.player.PlayerJoinLeaveListeners;
import camp.pvp.core.profiles.CoreProfileManager;
import camp.pvp.core.punishments.PunishmentManager;
import camp.pvp.core.ranks.RankManager;
import camp.pvp.core.chattags.ChatTagManager;
import camp.pvp.core.server.CoreServerManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotCore extends JavaPlugin {

    private @Getter static SpigotCore instance;

    private @Getter CoreServerManager coreServerManager;
    private @Getter ChatTagManager chatTagManager;
    private @Getter CoreProfileManager coreProfileManager;
    private @Getter PunishmentManager punishmentManager;
    private @Getter RankManager rankManager;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        this.coreServerManager = new CoreServerManager(this);
        this.chatTagManager = new ChatTagManager(this);
        this.rankManager = new RankManager(this);
        this.punishmentManager = new PunishmentManager(this);
        this.coreProfileManager = new CoreProfileManager(this);

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        coreServerManager.shutdown();
        coreProfileManager.shutdown();

        instance = null;
    }

    public void registerCommands() {
        new AltsCommand(this);
        new BanCommand(this);
        new BlacklistCommand(this);
        new ChatCommand(this);
        new DemoCommand(this);
        new GrantCommand(this);
        new GrantHistoryCommand(this);
        new GrantsCommand(this);
        new HelpOpCommand(this);
        new HistoryCommand(this);
        new IgnoreCommand(this);
        new InventorySeeCommand(this);
        new KickCommand(this);
        new ListCommand(this);
        new MessageCommand(this);
        new MoreCommand(this);
        new MuteCommand(this);
        new NameMcCommand(this);
        new RankCommand(this);
        new ReportCommand(this);
        new SoundsCommand(this);
        new StaffChatCommand(this);
        new StaffHistoryCommand(this);
        new TagManagerCommand(this);
        new TagsCommand(this);
        new TempBanCommand(this);
        new TempMuteCommand(this);
        new ToggleGlobalChatCommand(this);
        new ToggleMessagesCommand(this);
        new UnbanCommand(this);
        new UnblacklistCommand(this);
        new UnignoreCommand(this);
        new UnmuteCommand(this);
        new UserHistoryCommand(this);
    }

    public void registerListeners() {
        new PlayerChatListener(this);
        new PlayerCommandPreprocessListener(this);
        new PlayerJoinLeaveListeners(this);
        new PlayerTeleportListener(this);

        new MongoGuiListener(this);
    }
}