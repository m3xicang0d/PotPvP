package net.frozenorb.potpvp.player.setting.event;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.frozenorb.potpvp.player.setting.Setting;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when a player updates a setting value.
 */
public final class SettingUpdateEvent extends PlayerEvent {

    @Getter public static HandlerList handlerList = new HandlerList();

    /**
     * The setting that was updated
     */
    @Getter public final Setting setting;

    /**
     * The new state of the setting
     */
    @Getter public final boolean enabled;

    public SettingUpdateEvent(Player player, Setting setting, boolean enabled) {
        super(player);

        this.setting = Preconditions.checkNotNull(setting, "setting");
        this.enabled = enabled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}