package camp.pvp.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PlayerUtils {

    public static void showDemoScreen(Player player) {
        final String path = Bukkit.getServer().getClass().getPackage().getName();
        final String version = path.substring(path.lastIndexOf(".") + 1);
        try {
            final Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            final Class<?> PacketPlayOutGameStateChange = Class.forName("net.minecraft.server." + version + ".PacketPlayOutGameStateChange");
            final Class<?> Packet = Class.forName("net.minecraft.server." + version + ".Packet");
            final Constructor<?> playOutConstructor = PacketPlayOutGameStateChange.getConstructor(Integer.TYPE, Float.TYPE);
            final Object packet = playOutConstructor.newInstance(5, 0);
            final Object craftPlayerObject = craftPlayer.cast(player);
            final Method getHandleMethod = craftPlayer.getMethod("getHandle", (Class<?>[])new Class[0]);
            final Object handle = getHandleMethod.invoke(craftPlayerObject, new Object[0]);
            final Object pc = handle.getClass().getField("playerConnection").get(handle);
            final Method sendPacketMethod = pc.getClass().getMethod("sendPacket", Packet);
            sendPacketMethod.invoke(pc, packet);
        }
        catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchFieldException | NoSuchMethodException | SecurityException | InvocationTargetException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
