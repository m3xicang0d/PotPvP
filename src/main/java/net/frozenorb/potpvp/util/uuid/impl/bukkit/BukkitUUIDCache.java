package net.frozenorb.potpvp.util.uuid.impl.bukkit;


import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.util.uuid.IUUIDCache;

import java.util.UUID;

public final class BukkitUUIDCache implements IUUIDCache {

    public UUID uuid(String name) {
        return (PotPvPSI.getInstance().getServer().getOfflinePlayer(name).getUniqueId());
    }

    public String name(UUID uuid) {
        return (PotPvPSI.getInstance().getServer().getOfflinePlayer(uuid).getName());
    }

    public boolean cached(UUID uuid) {
        return PotPvPSI.getInstance().getServer().getOfflinePlayer(uuid) != null;
    }

    public boolean cached(String name) {
        return PotPvPSI.getInstance().getServer().getOfflinePlayer(name) != null;
    }

    public void ensure(UUID uuid) {} // Do nothing, as this class just delegates calls down to Bukkit.

    public void update(UUID uuid, String name) {} // We never need to update this, as this class just delegates calls down to Bukkit.

    public void updateAll(UUID uuid,String name) {} // We never need to update this, as this class just delegates calls down to Bukkit.

}