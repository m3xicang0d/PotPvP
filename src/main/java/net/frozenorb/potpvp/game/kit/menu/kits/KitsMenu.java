package net.frozenorb.potpvp.game.kit.menu.kits;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kit.Kit;
import net.frozenorb.potpvp.game.kit.KitHandler;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.game.kittype.menu.select.SelectKiToEditMenu;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.Menu;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.util.menu.MenuBackButton;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class KitsMenu extends Menu {

    public final KitType kitType;

    @Override
    public String getTitle(Player player) {
        return ("Viewing " + kitType.getDisplayName() + " kits");
    }

    public KitsMenu(KitType kitType) {
        this.setPlaceholder(true);
        this.setAutoUpdate(true);
        this.kitType = kitType;
    }

    @Override
    public void onClose(Player player) {
        InventoryUtils.resetInventoryDelayed(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        FileConfiguration config = PotPvPSI.getInstance().getConfig();
        KitHandler kitHandler = PotPvPSI.getInstance().getKitHandler();
        HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
        for (int kitSlot = 1; kitSlot <= 4; ++kitSlot) {
            Optional<Kit> kitOpt = kitHandler.getKit(player, this.kitType, kitSlot);
            int column = kitSlot * 2 - 1;
            buttons.put(this.getSlot(column, 0), new KitIconButton(kitOpt, this.kitType, kitSlot));
            buttons.put(this.getSlot(column, 2), new KitEditButton(kitOpt, this.kitType, kitSlot));
            if (kitOpt.isPresent()) {
                buttons.put(this.getSlot(column, 3), new KitRenameButton(kitOpt.get()));
                buttons.put(this.getSlot(column, 4), new KitDeleteButton(this.kitType, kitSlot));
                continue;
            }
            buttons.put(this.getSlot(column, 3), Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.GRAY.getWoolData(), ""));
            buttons.put(this.getSlot(column, 4), Button.placeholder(Material.STAINED_GLASS_PANE, DyeColor.GRAY.getWoolData(), ""));
        }
        buttons.put(this.getSlot(0, 4), new MenuBackButton(p -> new SelectKiToEditMenu(kitType -> new KitsMenu(kitType).openMenu(p), "Select a kit type...").openMenu(p)));
        buttons.put(this.getSlot(0, 4), new MenuBackButton(p -> new SelectKiToEditMenu(kitType -> new KitsMenu(kitType).openMenu(p), CC.translate(config.getString("TITLES.KIT-EDITOR"))).openMenu(p)));
        return buttons;
    }
}

