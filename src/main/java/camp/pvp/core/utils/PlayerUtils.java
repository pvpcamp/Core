package camp.pvp.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class PlayerUtils {

    public static void showDemoScreen(Player player) {
        String path = Bukkit.getServer().getClass().getPackage().getName();
        String version = path.substring(path.lastIndexOf(".") + 1);
        try {
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            Class<?> PacketPlayOutGameStateChange = Class.forName("net.minecraft.server." + version + ".PacketPlayOutGameStateChange");
            Class<?> Packet = Class.forName("net.minecraft.server." + version + ".Packet");
            Constructor<?> playOutConstructor = PacketPlayOutGameStateChange.getConstructor(Integer.TYPE, Float.TYPE);
            Object packet = playOutConstructor.newInstance(5, 0);
            Object craftPlayerObject = craftPlayer.cast(player);
            Method getHandleMethod = craftPlayer.getMethod("getHandle", (Class<?>[])new Class[0]);
            Object handle = getHandleMethod.invoke(craftPlayerObject, new Object[0]);
            Object pc = handle.getClass().getField("playerConnection").get(handle);
            Method sendPacketMethod = pc.getClass().getMethod("sendPacket", Packet);
            sendPacketMethod.invoke(pc, packet);
        }
        catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchFieldException | NoSuchMethodException | SecurityException | InvocationTargetException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void crashClient(Player player) {
        String path = Bukkit.getServer().getClass().getPackage().getName();
        String version = path.substring(path.lastIndexOf(".") + 1);
        try {
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            Class<?> PacketPlayOutGameStateChange = Class.forName("net.minecraft.server." + version + ".PacketPlayOutExplosion");
            Class<?> Packet = Class.forName("net.minecraft.server." + version + ".Packet");
            Class<?> Vec3D = Class.forName("net.minecraft.server." + version + ".Vec3D");
            Constructor<?> vec3dConstructor = Vec3D.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE);
            Object vector = vec3dConstructor.newInstance(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
            Constructor<?> playOutConstructor = PacketPlayOutGameStateChange.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE, Float.TYPE, List.class, Vec3D);
            Object packet = playOutConstructor.newInstance(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
                    Float.MAX_VALUE, Collections.EMPTY_LIST, vector);
            Object craftPlayerObject = craftPlayer.cast(player);
            Method getHandleMethod = craftPlayer.getMethod("getHandle");
            Object handle = getHandleMethod.invoke(craftPlayerObject);
            Object pc = handle.getClass().getField("playerConnection").get(handle);
            Method sendPacketMethod = pc.getClass().getMethod("sendPacket", Packet);
            sendPacketMethod.invoke(pc, packet);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
