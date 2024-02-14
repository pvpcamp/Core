package camp.pvp.core.profiles;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum FlightEffect {
    NONE, HEARTS, MAGIC, NOTES;

    public void playEffect(Player player) {

        if(!player.isFlying()) return;

        Effect effect = null;
        switch(this) {
            case HEARTS -> {
                effect = Effect.HEART;
            }
            case MAGIC -> {
                effect = Effect.PORTAL;
            }
            case NOTES -> {
                effect = Effect.NOTE;
            }
        }

        if(effect == null) return;

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(!p.canSee(player)) continue;

            Location location = player.getLocation();
            Location l = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
            p.playEffect(l, effect, null);
        }
    }

    @Override
    public String toString() {
        String name = this.name();
        name = name.replace("_", " ");
        return WordUtils.capitalizeFully(name);
    }
}
