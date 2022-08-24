package net.frozenorb.potpvp.integration.holograms;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.object.BackendAPI;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PracticeHologram {

    private String kitType;
    private Location location;
    private final Hologram hologram;
    private final ConfigFile config = PotPvPSI.instance.getHologramsConfig();

    public PracticeHologram(String kitType, Location location) {
        this.kitType = kitType;
        this.location = location;
        this.hologram = BackendAPI.createHologram(PotPvPSI.getInstance(), location);
        this.hologram.clearLines();
    }

    public void spawn() {
        List<String> lines = CC.translate(config.getStringList("HOLOGRAMS." + kitType + ".LINES"));
        HologramsHandler handler = PotPvPSI.getInstance().getHologramsHandler();
        hologram.getVisibilityManager().setVisibleByDefault(true);
        if (!hologram.getLocation().getChunk().isLoaded()) {
            hologram.getLocation().getChunk().load();
        }
        boolean isGlobal = kitType.equalsIgnoreCase("GLOBAL");
        for(int i = 0; i < lines.size(); i++) {
            String text = lines.get(i);
            Map<Integer, Map.Entry<String, Integer>> map = null;
            KitType kit = null;
            if(!isGlobal) {
                kit = KitType.byId(kitType);
                map = handler.getKitsPositions().get(kit);
            }
            for(int t = 1; t < 11; t++) {
                if(isGlobal) {
                    Map.Entry<String, Integer> entry = handler.getGlobalPositions().get(t);
                    if(entry != null) {
                        text = text.replace("%global-top-" + t + "%", entry.getKey());
                        text = text.replace("%elo-global-" + t + "%", String.valueOf(entry.getValue()));
                    } else {
                        text = text.replace("%global-top-" + t + "%", "-----");
                        text = text.replace("%elo-global-" + t + "%", "0");
                    }
                } else {
                    if(map != null) {
                        Map.Entry<String, Integer> entry = map.get(t);
                        text = text.replace("%kittype%", kit.getColoredDisplayName());
                        if (entry != null) {
                            text = text.replace("%top-" + t + "%", entry.getKey());
                            text = text.replace("%elo-" + t + "%", String.valueOf(entry.getValue()));
                        } else {
                            text = text.replace("%top-" + t + "%", "-----");
                            text = text.replace("%elo-" + t + "%", "0");
                        }
                    } else {
                        text = text.replace("%top-" + t + "%", "-----");
                        text = text.replace("%elo-" + t + "%", "0");
                    }
                }
                text = text.replace("%seconds%", String.valueOf(PotPvPSI.getInstance().getHologramsConfig().getInt("HOLOGRAMS.UPDATE-TIME")));
            }
            hologram.insertTextLine(i, text);
        }
        update();
    }

    public void update() {
        hologram.getVisibilityManager().setVisibleByDefault(true);
        hologram.getVisibilityManager().resetVisibilityAll();
    }

    public void destroy() {
        hologram.clearLines();
        hologram.delete();
    }
}
