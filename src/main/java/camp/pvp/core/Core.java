package camp.pvp.core;

import camp.pvp.core.api.CoreAPI;
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

    @Getter private static Core instance;
    @Getter private static CoreAPI api;

    @Getter private CoreServerManager coreServerManager;
    @Getter private ChatTagManager chatTagManager;
    @Getter private CoreProfileManager coreProfileManager;
    @Getter private PunishmentManager punishmentManager;
    @Getter private RankManager rankManager;

    @Getter private long upTime;

    @Override
    public void onEnable() {
        instance = this;
        api = new CoreAPI(this);

        upTime = System.currentTimeMillis();

        saveDefaultConfig();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        coreServerManager = new CoreServerManager(this);
        chatTagManager = new ChatTagManager(this);
        rankManager = new RankManager(this);
        punishmentManager = new PunishmentManager(this);
        coreProfileManager = new CoreProfileManager(this);

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
        new ColorCommand(this);
        new DemoCommand(this);
        new FeedCommand(this);
        new FlightEffectsCommand(this);
        new FlyCommand(this);
        new GamemodeCommand(this);
        new GrantCommand(this);
        new GrantHistoryCommand(this);
        new GrantsCommand(this);
        new HealCommand(this);
        new HelpOpCommand(this);
        new HistoryCommand(this);
        new IgnoreCommand(this);
        new InvalidateCommand(this);
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
        new ServersCommand(this);
        new SkullCommand(this);
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
        new TimeZoneCommand(this);
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
        new PlayerChatTabCompleteListener(this);
        new PlayerCommandPreprocessListener(this);
        new PlayerJoinLeaveListeners(this);
    }
}