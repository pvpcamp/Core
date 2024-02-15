package camp.pvp.core.profiles;

import camp.pvp.core.Core;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public enum FlightEffect {
    NONE, HEARTS, LAVA, MAGIC, NOTES;

    /***
     * Plays a 3 second visual effect for the player.
     * @param player
     */

    public void playEffect(Player player) {
        switch(this) {
            case HEARTS -> {
                new BukkitRunnable() {
                    int frame = 0;
                    @Override
                    public void run() {
                        if(frame == 15) {
                            cancel();
                        }

                        if(!player.isFlying()) {
                            frame++;
                            return;
                        }

                        Location playerLocation = player.getLocation();

                        double radius = 1.0;
                        double increment = 2 * Math.PI / 15;
                        double angle = frame * increment;
                        double x = playerLocation.getX() + radius * Math.cos(angle);
                        double z = playerLocation.getZ() + radius * Math.sin(angle);
                        Location point = new Location(playerLocation.getWorld(), x, playerLocation.getY() + 2.25, z);

                        playEffectServer(player, point, Effect.HEART);

                        if(frame % 5 == 0) {
                            Location randomLocation = player.getLocation();
                            randomLocation.add(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1);
                            playEffectServer(player, randomLocation, Effect.HEART);
                        }

                        frame++;
                    }
                }.runTaskTimer(Core.getInstance(), 0, 4);
            }
            case LAVA -> {
                new BukkitRunnable() {
                    int frame = 0;
                    @Override
                    public void run() {
                        if(frame == 15) {
                            cancel();
                        }

                        if(!player.isFlying()) {
                            frame++;
                            return;
                        }

                        Location playerLocation = player.getLocation();

                        double yawRadians = Math.toRadians(playerLocation.getYaw());

                        double xOffset = -0.75 * Math.sin(yawRadians);
                        double zOffset = 0.75 * Math.cos(yawRadians);

                        playerLocation.add(0, 1.25, 0);
                        playerLocation.subtract(xOffset, 0, zOffset);

                        playEffectServer(player, playerLocation, Effect.LAVA_POP);

                        frame++;
                    }
                }.runTaskTimer(Core.getInstance(), 0, 4);
            }
            case MAGIC -> {
                new BukkitRunnable() {
                    int frame = 0;
                    @Override
                    public void run() {
                        if(frame == 15) {
                            cancel();
                        }

                        if(!player.isFlying()) {
                            frame++;
                            return;
                        }

                        Location playerLocation = player.getLocation();

                        double radius = 1.5;
                        double increment = 2 * Math.PI / 15;
                        double angle = frame * increment;
                        double x = playerLocation.getX() + radius * Math.cos(angle);
                        double z = playerLocation.getZ() + radius * Math.sin(angle);
                        Location point = new Location(playerLocation.getWorld(), x, playerLocation.getY(), z);

                        playEffectServer(player, point, Effect.WATERDRIP);
                        playEffectServer(player, playerLocation, Effect.PORTAL);

                        frame++;
                    }
                }.runTaskTimer(Core.getInstance(), 0, 4);
            }
            case NOTES -> {
                new BukkitRunnable() {
                    int frame = 0;
                    @Override
                    public void run() {
                        if(frame == 30) {
                            cancel();
                        }

                        if(!player.isFlying()) {
                            frame++;
                            return;
                        }

                        Location playerLocation = player.getLocation();
                        playerLocation.subtract(0, .25, 0);

                        double radius = 1.5;
                        double increment = 2 * Math.PI / 30;
                        double angle = (frame - 1) * increment;
                        double x = playerLocation.getX() + radius * Math.cos(angle);
                        double z = playerLocation.getZ() + radius * Math.sin(angle);
                        Location point = new Location(playerLocation.getWorld(), x, playerLocation.getY(), z);

                        playEffectServer(player, point, Effect.NOTE);

                        if(frame % 15 == 0) {
                            Location randomLocation = player.getLocation();
                            randomLocation.add(Math.random() * 2 - 1, Math.random() * 2, Math.random() * 2 - 1);
                            playEffectServer(player, randomLocation, Effect.NOTE);
                        }

                        frame++;
                    }
                }.runTaskTimer(Core.getInstance(), 0, 2);
            }
        }
    }

    @Override
    public String toString() {
        String name = this.name();
        name = name.replace("_", " ");
        return WordUtils.capitalizeFully(name);
    }

    private void playEffectServer(Player player, Location location, Effect effect) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(!p.getWorld().equals(player.getWorld())) continue;

            if(p.getLocation().distance(location) < 20 && p.canSee(player)) {
                p.playEffect(location, effect, null);
            }
        }
    }
}
