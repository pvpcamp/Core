package camp.pvp.core.chattags;

import camp.pvp.core.Core;
import camp.pvp.core.listeners.redis.RedisTagUpdateListener;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.mongo.MongoIterableResult;
import camp.pvp.mongo.MongoManager;
import camp.pvp.mongo.MongoUpdate;
import camp.pvp.redis.RedisPublisher;
import camp.pvp.redis.RedisSubscriber;
import com.google.gson.JsonObject;
import com.mongodb.client.FindIterable;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class ChatTagManager {

    private Core plugin;
    private Map<UUID, ChatTag> chatTags;

    private MongoManager mongoManager;
    private String tagsCollection;

    private RedisPublisher redisPublisher;
    private RedisSubscriber tagUpdateSubscriber;
    public ChatTagManager(Core plugin) {
        this.plugin = plugin;
        this.chatTags = new HashMap<>();

        plugin.getLogger().info("Importing ranks from MongoDB.");

        FileConfiguration config = plugin.getConfig();

        this.mongoManager = new MongoManager(plugin, config.getString("networking.mongo.uri"), config.getString("networking.mongo.database"));
        this.tagsCollection = config.getString("networking.mongo.tags_collection");

        this.redisPublisher = new RedisPublisher(plugin, config.getString("networking.redis.host"), config.getInt("networking.redis.port"));
        this.tagUpdateSubscriber = new RedisSubscriber(
                plugin,
                config.getString("networking.redis.host"),
                config.getInt("networking.redis.port"),
                "core_tag_updates",
                new RedisTagUpdateListener(plugin, this));

        getMongoManager().getCollectionIterable(false, tagsCollection, new MongoIterableResult() {
            @Override
            public void call(FindIterable<Document> iterable) {
                iterable.forEach(
                        document -> {
                            UUID uuid = document.get("_id", UUID.class);
                            ChatTag tag = new ChatTag(uuid);
                            tag.importFromDocument(document);
                            chatTags.put(uuid, tag);
                        }
                );
            }
        });

        if(chatTags.isEmpty()) {
            plugin.getLogger().info("No tags were found in the database.");
        } else {
            plugin.getLogger().info("Imported " + chatTags.size() + " tags from the database.");
        }
    }

    public ChatTag getTagFromName(String name) {
        for(ChatTag tag : getChatTags().values()) {
            if(tag.getName().equalsIgnoreCase(name)) {
                return tag;
            }
        }

        return null;
    }

    public ChatTag importFromDatabase(UUID uuid) {
        final ChatTag[] tag = {null};
        getMongoManager().getDocument(false, tagsCollection, uuid, document -> {
            if(document != null) {
                tag[0] = new ChatTag(uuid);
                tag[0].importFromDocument(document);
                getChatTags().put(uuid, tag[0]);
            }
        });

        return tag[0];
    }

    public ChatTag create(String name) {
        ChatTag tag = new ChatTag(UUID.randomUUID());
        tag.setName(name.toLowerCase());
        tag.setDisplayName(name);
        tag.setTag(name);
        getChatTags().put(tag.getUuid(), tag);

        exportToDatabase(tag, false);
        return tag;
    }

    public void delete(ChatTag tag) {
        getChatTags().remove(tag.getUuid());

        for(CoreProfile profile : plugin.getCoreProfileManager().getLoadedProfiles().values()) {
            profile.getOwnedChatTags().remove(tag);
        }

        getMongoManager().deleteDocument(true, tagsCollection, tag.getUuid());

        Bukkit.getScheduler().runTaskLater(getPlugin(), ()-> sendRedisUpdate(tag, true), 10);
    }

    public void exportToDatabase(ChatTag tag, boolean async) {
        MongoUpdate mu = new MongoUpdate(tagsCollection, tag.getUuid());
        mu.setUpdate(tag.exportToMap());
        getMongoManager().massUpdate(async, mu);

        if(async) {
            Bukkit.getScheduler().runTaskLater(getPlugin(), ()-> this.sendRedisUpdate(tag, false), 10);
        } else {
            sendRedisUpdate(tag, false);
        }
    }

    public void sendRedisUpdate(ChatTag tag, boolean deleted) {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", tag.getUuid().toString());

        String server = getPlugin().getCoreServerManager().getCoreServer().getName();
        json.addProperty("from_server", server);
        json.addProperty("deleted", deleted);

        getRedisPublisher().publishMessage("core_tag_updates", json);
    }
}
