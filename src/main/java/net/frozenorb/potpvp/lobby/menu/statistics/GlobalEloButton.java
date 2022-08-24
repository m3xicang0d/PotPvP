package net.frozenorb.potpvp.lobby.menu.statistics;

import com.google.common.collect.Lists;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map.Entry;

public class GlobalEloButton extends Button {

    private final FileConfiguration config = PotPvPSI.getInstance().getConfig();

    @Override
    public String getName(Player player) {
        return CC.translate(config.getString("SETTINGS.LEADERBOARDS.GLOBAL"));
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();
        description.add(CC.translate("&7&m----------------"));
        int counter = 1;
        for (Entry<String, Integer> entry : PotPvPSI.getInstance().getEloHandler().topElo(null).entrySet()) {
            description.add(CC.translate("&6#" + counter + " &r" + entry.getKey() + " &7- " + "&f" + entry.getValue()));
            counter++;
        }
        description.add(CC.translate("&7&m----------------"));

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.NETHER_STAR;
    }
}
