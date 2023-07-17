package camp.pvp.core;

import camp.pvp.NetworkHelper;
import camp.pvp.core.commands.RankCommand;
import camp.pvp.core.listeners.player.PlayerChatListener;
import camp.pvp.core.listeners.player.PlayerJoinLeaveListeners;
import camp.pvp.core.profiles.CoreProfileManager;
import camp.pvp.core.punishments.PunishmentManager;
import camp.pvp.core.ranks.RankManager;
import camp.pvp.core.chattags.ChatTagManager;
import camp.pvp.mongo.MongoManager;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotCore extends JavaPlugin {

    private @Getter static SpigotCore instance;

    private @Getter MongoManager mongoManager;

    private @Getter ChatTagManager chatTagManager;
    private @Getter CoreProfileManager coreProfileManager;
    private @Getter PunishmentManager punishmentManager;
    private @Getter RankManager rankManager;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

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
        new RankCommand(this);
    }

    public void registerListeners() {
        new PlayerChatListener(this);
        new PlayerJoinLeaveListeners(this);
    }
}
