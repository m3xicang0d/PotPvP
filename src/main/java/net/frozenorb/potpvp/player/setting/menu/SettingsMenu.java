

package net.frozenorb.potpvp.player.setting.menu;

import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.Menu;
import net.frozenorb.potpvp.player.setting.Setting;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SettingsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return ("Edit settings");
    }

    public SettingsMenu() {
        this.setAutoUpdate(true);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;
        for (Setting setting : Setting.values()) {
            if (setting.canUpdate(player)) {
                buttons.put(index++, new SettingButton(setting));
            }
        }
        return buttons;
    }
}
