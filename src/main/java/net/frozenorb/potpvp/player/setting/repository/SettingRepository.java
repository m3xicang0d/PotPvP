package net.frozenorb.potpvp.player.setting.repository;

import net.frozenorb.potpvp.player.setting.Setting;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public interface SettingRepository {

    Map<Setting, Boolean> loadSettings(UUID playerUuid) throws IOException;

    void saveSettings(UUID playerUuid, Map<Setting, Boolean> settings) throws IOException;

}