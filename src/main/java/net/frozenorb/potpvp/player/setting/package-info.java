/**
 * Handles accessing, saving, updating, and presentation of player settings.
 * <p>
 * This includes the /settings command, a settings menu, persistence, etc.
 * Clients using the settings API should only concern themselves with {@link net.frozenorb.potpvp.player.setting.event.SettingUpdateEvent},
 * {@link net.frozenorb.potpvp.player.setting.SettingHandler#getSetting(java.util.UUID, Setting)} and
 * {@link net.frozenorb.potpvp.player.setting.SettingHandler#updateSetting(org.bukkit.entity.Player, Setting, boolean)},
 */
package net.frozenorb.potpvp.player.setting;

