package camp.pvp.core;

import camp.pvp.NetworkHelper;
import camp.pvp.command.CommandHandler;
import camp.pvp.core.commands.*;
import camp.pvp.core.listeners.mongo.MongoGuiListener;
import camp.pvp.core.listeners.pearls.PlayerTeleportListener;
import camp.pvp.core.listeners.player.PlayerChatListener;
import camp.pvp.core.listeners.player.PlayerChatTabCompleteListener;
import camp.pvp.core.listeners.player.PlayerCommandPreprocessListener;
import camp.pvp.core.listeners.player.PlayerJoinLeaveListeners;
import camp.pvp.core.profiles.CoreProfileManager;
import camp.pvp.core.punishments.PunishmentManager;
import camp.pvp.core.ranks.RankManager;
import camp.pvp.core.chattags.ChatTagManager;
import camp.pvp.core.server.CoreServerManager;
import camp.pvp.core.tablist.Tab;
import camp.pvp.core.tablist.TabAdapter;
import camp.pvp.core.tablist.Tablist;
import io.github.thatkawaiisam.ziggurat.Ziggurat;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotCore extends JavaPlugin {

    private @Getter static SpigotCore instance;

    private @Getter CoreServerManager coreServerManager;
    private @Getter ChatTagManager chatTagManager;
    private @Getter CoreProfileManager coreProfileManager;
    private @Getter PunishmentManager punishmentManager;
    private @Getter RankManager rankManager;
    private @Getter Tablist tablist;
    private Ziggurat ziggurat;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        this.coreServerManager = new CoreServerManager(this);
        this.chatTagManager = new ChatTagManager(this);
        this.rankManager = new RankManager(this);
        this.punishmentManager = new PunishmentManager(this);
        this.coreProfileManager = new CoreProfileManager(this);

        registerCommands();
        registerListeners();

//        this.tablist = new Tab();
//        ziggurat = new Ziggurat(this, new TabAdapter(tablist));
//        ziggurat.setTicks(5);
//        ziggurat.setHook(true);
    }

    @Override
    public void onDisable() {
        coreServerManager.shutdown();
        coreProfileManager.shutdown();

        instance = null;
    }

    public void registerCommands() {
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
        new WipePunishmentsCommand(this);

        CommandHandler commandHandler = NetworkHelper.getInstance().getCommandHandler();
        commandHandler.registerCommand(new AltsCommand());
        commandHandler.registerCommand(new FeedCommand());
        commandHandler.registerCommand(new FlyCommand());
        commandHandler.registerCommand(new GamemodeCommand());
        commandHandler.registerCommand(new HealCommand());
        commandHandler.registerCommand(new PlayCommand(this));
        commandHandler.registerCommand(new PlayerLookupCommand());
        commandHandler.registerCommand(new PlaytimeCommand());
        commandHandler.registerCommand(new SeenCommand());
        commandHandler.registerCommand(new ServerInfoCommand(this));
        commandHandler.registerCommand(new TagCommand(this));
    }

    public void registerListeners() {
        new PlayerChatListener(this);
        new PlayerChatTabCompleteListener(this);
        new PlayerCommandPreprocessListener(this);
        new PlayerJoinLeaveListeners(this);
        new PlayerTeleportListener(this);

        new MongoGuiListener(this);
    }
}