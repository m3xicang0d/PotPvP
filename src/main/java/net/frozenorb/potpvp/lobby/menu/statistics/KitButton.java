package net.frozenorb.potpvp.lobby.menu.statistics;

import com.google.common.collect.Lists;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map.Entry;

public class KitButton extends Button {

    private final FileConfiguration config = PotPvPSI.getInstance().getConfig();
    public KitType kitType;

    public KitButton(KitType kitType) {
        this.kitType = kitType;
    }

    @Override
    public String getName(Player player) {
        return CC.translate(config.getString("SETTINGS.LEADERBOARDS.KITS").replace("%kittype%", kitType.displayName));
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();

        description.add(CC.translate("&7&m----------------"));

        int counter = 1;

        for (Entry<String, Integer> entry : PotPvPSI.getInstance().getEloHandler().topElo(kitType).entrySet()) {
            description.add(CC.translate("&6#" + counter + " &f" + entry.getKey() + " &7- &f" + entry.getValue()));

            counter++;
        }

        description.add(CC.translate("&7&m----------------"));

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return kitType.getIcon().getItemType();
    }

    @Override
    public byte getDamageValue(Player player) {
        return kitType.getIcon().getData();
    }
}
