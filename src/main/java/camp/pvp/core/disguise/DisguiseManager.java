package camp.pvp.core.disguise;

import camp.pvp.core.Core;
import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.ranks.Rank;
import camp.pvp.practice.Practice;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DisguiseManager {

    private Core plugin;
    private @Getter List<String> adjectives, nouns;
    private @Getter List<UUID> skins;
    private @Getter Map<UUID, String> disguiseMap;
    private Map<UUID, Rank> rankMap = new HashMap<>();

    public DisguiseManager(Core plugin) {
        this.plugin = plugin;
        disguiseMap = new HashMap<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            adjectives = getLines("https://gist.githubusercontent.com/hugsy/8910dc78d208e40de42deb29e62df913/raw/eec99c5597a73f6a9240cab26965a8609fa0f6ea/english-adjectives.txt");
            nouns = getLines("https://gist.githubusercontent.com/hugsy/8910dc78d208e40de42deb29e62df913/raw/eec99c5597a73f6a9240cab26965a8609fa0f6ea/english-nouns.txt");
            skins = randomSkin();
        });
    }

    public boolean isDisguised(Player player) {
        return disguiseMap.containsKey(player.getUniqueId());
    }

    public String getRealUsername(Player player) {
        CoreProfile coreProfile = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId());
        return coreProfile.getName();
    }

    public String getDisguisedName(Player player) {
        return disguiseMap.get(player.getUniqueId());
    }

    public Rank getRank(Player player) {
        return rankMap.get(player.getUniqueId());
    }

    public void disguise(Player player, String disguise, Rank rank, boolean skin, boolean remove) {

        EntityPlayer originalPlayer = ((CraftPlayer) player).getHandle();
        GameProfile gameProfile = originalPlayer.getProfile();
        UUID uuid = UUIDFetcher.getUUID(disguise);
        String finalDisguise = (uuid != null ? UUIDFetcher.getName(uuid) : disguise);

        try {
            Field nameField = GameProfile.class.getDeclaredField("name");
            Field modField = Field.class.getDeclaredField("modifiers");
            nameField.setAccessible(true);
            modField.setAccessible(true);
            modField.setInt(nameField, nameField.getModifiers() & -17);
            nameField.set(gameProfile, finalDisguise);
        } catch (Exception ex) {
            player.sendMessage(ChatColor.RED + "Error occurred while trying to disguise.");
            return;
        }

        rankMap.put(player.getUniqueId(), rank);

        if (skin) {
            try {
                if (uuid == null) {
                    String[] steve = getSkin("Steve");
                    gameProfile.getProperties().clear();
                    gameProfile.getProperties().put("textures", new Property(steve[0], steve[1], steve[2]));
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.getOnlinePlayers().forEach(p -> {
                            if (p != player) {
                                p.hidePlayer(player);
                                p.showPlayer(player);
                            }
                        });
                    });
                } else {
                    String[] disguiseSkin = getSkin(finalDisguise);
                    gameProfile.getProperties().clear();
                    gameProfile.getProperties().put("textures", new Property(disguiseSkin[0], disguiseSkin[1], disguiseSkin[2]));
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.getOnlinePlayers().forEach(p -> {
                            if (p != player) {
                                p.hidePlayer(player);
                                p.showPlayer(player);
                            }
                        });
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else {
            Random random = new Random();
            int num = random.nextInt(20000 + 1);
            String[] randomSkin = getSkin(skins.get(num));
            gameProfile.getProperties().clear();
            gameProfile.getProperties().put("textures", new Property(randomSkin[0], randomSkin[1], randomSkin[2]));
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if (p != player) {
                        p.hidePlayer(player);
                        p.showPlayer(player);
                    }
                });
            });
        }

        if (!remove) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
                entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, originalPlayer));
                entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, originalPlayer));
                if (p != player) {
                    entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityId()));
                    entityPlayer.playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(originalPlayer));
                }
                if (p == player) {
                    Location location = p.getLocation().clone();
                    entityPlayer.playerConnection.sendPacket(new PacketPlayOutRespawn(entityPlayer.dimension, entityPlayer.getWorld().getDifficulty(), entityPlayer.getWorld().getWorldData().getType(), entityPlayer.playerInteractManager.getGameMode()));
                    p.teleport(location);
                    p.updateInventory();
                }
            });
        } else {
            Bukkit.getOnlinePlayers().forEach(p -> {
                EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
                entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, originalPlayer));
                entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, originalPlayer));
                if (p != player) {
                    entityPlayer.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityId()));
                }
            });
        }
        if (uuid == null && skin) {
            player.sendMessage(ChatColor.RED + "The username you tried to disguise as doesn't exist, using Steve skin.");
        }
        disguiseMap.put(player.getUniqueId(), disguise);
    }

    public void undisguise(Player player, boolean remove) {
        Rank rank = plugin.getCoreProfileManager().getLoadedProfiles().get(player.getUniqueId()).getHighestRank();
        disguise(player, getRealUsername(player), rank, true, remove);
        disguiseMap.remove(player.getUniqueId());
        rankMap.remove(player.getUniqueId());
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
    @SneakyThrows
    public static String[] getSkin(UUID uuid) {
        final URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
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

    public boolean checkState(Player player) {
        if (plugin.getServer().getPluginManager().getPlugin("Practice") != null) {
            return Practice.getInstance().getGameProfileManager().getLoadedProfiles().get(player.getUniqueId()).getState() == camp.pvp.practice.profiles.GameProfile.State.LOBBY;
        } else {
            return true;
        }
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
            lines.remove(lines.size() - 1);
            lines.remove(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lines;
    }

    public static List<UUID> randomSkin() {
        try {
            InputStream input = new URL("https://api.namemc.com/server/minemen.club/likes").openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            int cp;
            while ((cp = reader.read()) != -1) {
                stringBuilder.append((char) cp);
            }

            List<UUID> uuids = new ArrayList<>();
            org.json.JSONArray jsonArray = new org.json.JSONArray(stringBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    UUID uuid = UUID.fromString(jsonArray.getString(i));
                    uuids.add(uuid);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            return uuids;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}