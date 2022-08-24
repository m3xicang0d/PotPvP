package net.frozenorb.potpvp.lobby.menu.statistics;

import com.google.common.collect.Lists;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.bukkit.SkullBuilder;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerButton extends Button {

    private final FileConfiguration config = PotPvPSI.getInstance().getConfig();

    @Override
    public String getName(Player player) {
        return CC.translate(config.getString("SETTINGS.LEADERBOARDS.PLAYER").replace("%player%", player.getName()));
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();

        description.add(CC.translate("&7&m----------------"));
        for (KitType kitType : KitType.getAllTypes()) {
            if (kitType.isSupportsRanked()) {
                description.add(CC.translate("&f" + kitType.getDisplayName() + " &7- &f" + PotPvPSI.getInstance().getEloHandler().getElo(player, kitType)));
            }
        }
        description.add(CC.translate("&7&m----------------"));
        
        return description;
    }

    @Override
    public Material getMaterial(Player var1) {
        return null;
    }

    @Override
    public ItemStack getButtonItem(@NotNull Player player) {
        return new SkullBuilder(player)
                .setName(getName(player))
                .setLore(getDescription(player))
                .build();
    }
}
