

package net.frozenorb.potpvp.player.setting.menu;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.player.setting.Setting;
import net.frozenorb.potpvp.player.setting.SettingHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryView;

import java.util.ArrayList;
import java.util.List;

final class SettingButton extends Button
{
    public static final String ENABLED_ARROW;
    public static final String DISABLED_SPACER = "    ";
    public final Setting setting;

    SettingButton(final Setting setting) {
        this.setting = Preconditions.checkNotNull(setting, "setting");
    }

    @Override
    public String getName(final Player player) {
        return this.setting.getName();
    }

    @Override
    public List<String> getDescription(final Player player) {
        final List<String> description = new ArrayList<>();
        description.add("");
        description.addAll(this.setting.getDescription());
        description.add("");
        if (PotPvPSI.getInstance().getSettingHandler().getSetting(player, this.setting)) {
            description.add(SettingButton.ENABLED_ARROW + this.setting.getEnabledText());
            description.add("    " + this.setting.getDisabledText());
        }
        else {
            description.add("    " + this.setting.getEnabledText());
            description.add(SettingButton.ENABLED_ARROW + this.setting.getDisabledText());
        }
        return description;
    }

    @Override
    public Material getMaterial(final Player player) {
        return this.setting.getIcon();
    }

    @Override
    public void clicked(final Player player, final int slot, final ClickType clickType) {
        if (!this.setting.canUpdate(player)) {
            return;
        }
        final SettingHandler settingHandler = PotPvPSI.getInstance().getSettingHandler();
        final boolean enabled = !settingHandler.getSetting(player, this.setting);
        settingHandler.updateSetting(player, this.setting, enabled);
    }

    static {
        ENABLED_ARROW = ChatColor.GREEN + "  • ";
    }
}
