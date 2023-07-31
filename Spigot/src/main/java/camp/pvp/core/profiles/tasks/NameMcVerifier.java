package camp.pvp.core.profiles.tasks;

import camp.pvp.core.profiles.CoreProfile;
import camp.pvp.core.profiles.CoreProfileManager;
import camp.pvp.core.ranks.Rank;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NameMcVerifier implements Runnable{

    private CoreProfileManager coreProfileManager;
    public NameMcVerifier(CoreProfileManager cpm) {
        this.coreProfileManager = cpm;
    }

    @Override
    public void run() {
        try {
            InputStream input = new URL("https://api.namemc.com/server/" + coreProfileManager.getPlugin().getConfig().getString("namemc.api_server") + "/likes").openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            int cp;
            while ((cp = reader.read()) != -1) {
                stringBuilder.append((char) cp);
            }

            List<UUID> uuids = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    UUID uuid = UUID.fromString(jsonArray.getString(i));
                    uuids.add(uuid);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

            if(!uuids.isEmpty()) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    CoreProfile profile = coreProfileManager.getLoadedProfiles().get(player.getUniqueId());
                    if(uuids.contains(player.getUniqueId())) {
                        if(!profile.isNamemc()) {
                            profile.setNamemc(true);
                            for (Rank rank : coreProfileManager.getPlugin().getRankManager().getRanks().values()) {
                                if (rank.isNameMcAward()) {
                                    profile.getRanks().add(rank);
                                }
                            }

                            coreProfileManager.updatePermissions(profile);
                        }
                    } else {
                        if(profile.isNamemc()) {
                            profile.setNamemc(false);
                            profile.getRanks().removeIf(Rank::isNameMcAward);

                            coreProfileManager.updatePermissions(profile);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
