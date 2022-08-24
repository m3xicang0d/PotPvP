package net.frozenorb.potpvp.integration.holograms.task;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.integration.holograms.HologramsHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HologramsTask extends BukkitRunnable {

    @Override
    public void run() {
        HologramsHandler handler = PotPvPSI.getInstance().getHologramsHandler();
        handler.getGlobalPositions().clear();
        Set<Map.Entry<String, Integer>> globalEntries = PotPvPSI.getInstance().getEloHandler().topElo(null).entrySet();
        int position = 1;

        for (Map.Entry<String, Integer> entry : globalEntries) {
            if(position > 10) {
                break;
            }
            handler.getGlobalPositions().put(position, entry);
            position++;
        }

        handler.getKitsPositions().clear();
        KitType.getAllTypes().stream().filter(KitType::isSupportsRanked).forEach(kitType -> {
            Map<Integer, Map.Entry<String, Integer>> map = new HashMap<>();
            int i = 0;
            for(Map.Entry<String, Integer> entry : PotPvPSI.getInstance().getEloHandler().topElo(kitType).entrySet()) {
                if(i > 10) {
                    break;
                }
                map.put(i, entry);
                i++;
            }
            handler.getKitsPositions().put(kitType, map);
        });
        HologramsHandler.holograms.forEach(holo -> {
           holo.getHologram().clearLines();
           holo.spawn();
        });
    }
}
