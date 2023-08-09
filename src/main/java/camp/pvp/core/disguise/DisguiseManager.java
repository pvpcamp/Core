package camp.pvp.core.disguise;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.ranks.Rank;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DisguiseManager {

    private Core plugin;
    private @Getter List<String> adjectives, nouns, skins;
    private Map<UUID, String> disguiseMap = new HashMap<>();
    private Map<UUID, Rank> rankMap = new HashMap<>();

    public DisguiseManager(Core plugin) {
        this.plugin = plugin;
        CompletableFuture.runAsync(() -> {
            skins = getLines("https://api.namemc.com/server/minemen.club/likes");
            adjectives = getLines("https://gist.githubusercontent.com/hugsy/8910dc78d208e40de42deb29e62df913/raw/eec99c5597a73f6a9240cab26965a8609fa0f6ea/english-adjectives.txt");
            nouns = getLines("https://gist.githubusercontent.com/hugsy/8910dc78d208e40de42deb29e62df913/raw/eec99c5597a73f6a9240cab26965a8609fa0f6ea/english-nouns.txt");
        });
    }

    public boolean isDisguised(Player player) {
        return disguiseMap.containsKey(player.getUniqueId());
    }

    public String getRealUsername(Player player) {
        CoreProfile coreProfile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
        return coreProfile.getName();
    }

    public Rank getRank(Player player) {
        return rankMap.get(player.getUniqueId());
    }

    public void disguise(Player player, String disguise, Rank rank, boolean skin) {
        EntityPlayer originalPlayer = ((CraftPlayer) player).getHandle();
        GameProfile gameProfile = originalPlayer.getProfile();

        try {
            Field nameField = GameProfile.class.getDeclaredField("name");
            Field modField = Field.class.getDeclaredField("modifiers");
            nameField.setAccessible(true);
            modField.setAccessible(true);
            modField.setInt(nameField, nameField.getModifiers() & -17);
            nameField.set(gameProfile, disguise);
        } catch (Exception ex) {
            player.sendMessage(ChatColor.RED + "Error occurred while trying to disguise.");
            return;
        }

        if (rank != null) {
            rankMap.put(player.getUniqueId(), rank);
        }

        if (skin) {
            try {
                String[] disguiseSkin = getSkin(disguise);
                gameProfile.getProperties().clear();
                gameProfile.getProperties().put("textures", new Property(disguiseSkin[0], disguiseSkin[1], disguiseSkin[2]));
                Bukkit.getOnlinePlayers().forEach(p -> {
                    p.hidePlayer(player);
                    p.showPlayer(player);
                });
            } catch (Exception ex) {
                player.sendMessage(ChatColor.RED + "Error occurred while trying to disguise.");
                return;
            }
        }

        Bukkit.getOnlinePlayers().forEach(p -> {
            EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, originalPlayer));
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, originalPlayer));
            if (p != player) {
                entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityId()));
                entityPlayer.playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(originalPlayer));
            }
        });
        disguiseMap.put(player.getUniqueId(), disguise);
    }

    public void undisguise(Player player) {
        disguise(player, getRealUsername(player), null, true);
        disguiseMap.remove(player.getUniqueId());
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.hidePlayer(player);
            p.showPlayer(player);
        });
    }

    @SneakyThrows
    public static String[] getSkin(String name) {
        final URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDFetcher.getUUID(name).toString() + "?unsigned=false");
        final URLConnection uc = url.openConnection();
        uc.setUseCaches(false);
        uc.setDefaultUseCaches(false);
        uc.addRequestProperty("User-Agent", "Mozilla/5.0");
        uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
        uc.addRequestProperty("Pragma", "no-cache");
        final String json = new Scanner(uc.getInputStream(), "UTF-8").useDelimiter("\\A").next();
        final JSONParser parser = new JSONParser();
        final Object obj = parser.parse(json);
        final JSONArray properties = (JSONArray) ((JSONObject) obj).get("properties");
        for (int i = 0; i < properties.size(); ++i) {
            final JSONObject property = (JSONObject) properties.get(i);
            final String skinName = (String) property.get("name");
            final String value = (String) property.get("value");
            final String signature = property.containsKey("signature") ? ((String) property.get("signature")) : null;

            return new String[]{skinName, value, signature};
        }
        return null;
    }

    public static List<String> getLines(String link) {
        List<String> lines = new ArrayList<>();
        try {
            URL url = new URL(link);
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lines;
    }
}
