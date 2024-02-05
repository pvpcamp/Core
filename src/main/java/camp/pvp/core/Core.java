package camp.pvp.core;

import camp.pvp.core.commands.*;
import camp.pvp.core.listeners.player.PlayerChatListener;
import camp.pvp.core.listeners.player.PlayerChatTabCompleteListener;
import camp.pvp.core.listeners.player.PlayerCommandPreprocessListener;
import camp.pvp.core.listeners.player.PlayerJoinLeaveListeners;
import camp.pvp.core.profiles.CoreProfileManager;
import camp.pvp.core.punishments.PunishmentManager;
import camp.pvp.core.ranks.RankManager;
import camp.pvp.core.chattags.ChatTagManager;
import camp.pvp.core.server.CoreServerManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin {

    private @Getter static Core instance;

    private @Getter CoreServerManager coreServerManager;
    private @Getter ChatTagManager chatTagManager;
    private @Getter CoreProfileManager coreProfileManager;
    private @Getter PunishmentManager punishmentManager;
    private @Getter RankManager rankManager;
    @Getter long upTime;

    @Override
    public void onEnable() {
        instance = this;
        upTime = System.currentTimeMillis();

        this.saveDefaultConfig();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

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
        new AlertCommand(this);
        new AltsCommand(this);
        new AuthCommand(this);
        new BanCommand(this);
        new BlacklistCommand(this);
        new ChatCommand(this);
        new DemoCommand(this);
        new FeedCommand(this);
        new FlyCommand(this);
        new GamemodeCommand(this);
        new GrantCommand(this);
        new GrantHistoryCommand(this);
        new GrantsCommand(this);
        new HealCommand(this);
        new HelpOpCommand(this);
        new HistoryCommand(this);
        new IgnoreCommand(this);
        new InventorySeeCommand(this);
        new KickCommand(this);
        new ListCommand(this);
        new MessageCommand(this);
        new MoreCommand(this);
        new MuteCommand(this);
        new PlayerLookupCommand(this);
        new PlaytimeCommand(this);
        new RankCommand(this);
        new ReportCommand(this);
        new SeenCommand(this);
        new ServerInfoCommand(this);
        new SoundsCommand(this);
        new StaffChatCommand(this);
        new StaffHistoryCommand(this);
        new SudoCommand(this);
        new TagCommand(this);
        new TagManagerCommand(this);
        new TagsCommand(this);
        new TeleportCommand(this);
        new TempBanCommand(this);
        new TempMuteCommand(this);
        new ToggleGlobalChatCommand(this);
        new ToggleMessagesCommand(this);
        new UnbanCommand(this);
        new UnblacklistCommand(this);
        new UnignoreCommand(this);
        new UnmuteCommand(this);
        new UserHistoryCommand(this);
        new WipePunishmentsCommand(this);
    }

    public void registerListeners() {
        new PlayerChatListener(this);
        new PlayerChatTabCompleteListener(this);
        new PlayerCommandPreprocessListener(this);
        new PlayerJoinLeaveListeners(this);
    }
}