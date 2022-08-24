package net.frozenorb.potpvp.player.elo;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.player.elo.listener.EloLoadListener;
import net.frozenorb.potpvp.player.elo.listener.EloUpdateListener;
import net.frozenorb.potpvp.player.elo.repository.EloRepository;
import net.frozenorb.potpvp.player.elo.repository.MongoEloRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class EloHandler {

    public static final int DEFAULT_ELO = 1000;

    public final Map<Set<UUID>, Map<KitType, Integer>> eloData = new ConcurrentHashMap<>();
    @Getter public final EloRepository eloRepository;

    public EloHandler() {
        Bukkit.getPluginManager().registerEvents(new EloLoadListener(this), PotPvPSI.getInstance());
        Bukkit.getPluginManager().registerEvents(new EloUpdateListener(this, new EloCalculator(
            35, // k power
            7,
            25,
            7,
            25
        )), PotPvPSI.getInstance());

        eloRepository = new MongoEloRepository();
    }

    public int getElo(Player player, KitType kitType) {
        return getElo(ImmutableSet.of(player.getUniqueId()), kitType);
    }

    public void setElo(UUID uuid, KitType kitType, int newElo) {
        setElo(ImmutableSet.of(uuid), kitType, newElo);
    }

    public void setElo(Player player, KitType kitType, int newElo) {
        setElo(player.getUniqueId(), kitType, newElo);
    }

    public int getElo(Set<UUID> playerUuids, KitType kitType) {
        Map<KitType, Integer> partyElo = eloData.getOrDefault(playerUuids, ImmutableMap.of());
        return partyElo.getOrDefault(kitType, DEFAULT_ELO);
    }

    public int getGlobalElo(UUID uuid) {
        Map<KitType, Integer> eloValues = eloData.getOrDefault(ImmutableSet.of(uuid), ImmutableMap.of());
        if (eloValues.isEmpty()) return EloHandler.DEFAULT_ELO;
        int[] wrapper = new int[2];
        KitType.getAllTypes().stream().filter(kit -> kit.isSupportsRanked()).forEach(kitType -> {
            wrapper[0] = wrapper[0] + 1;
            wrapper[1] = wrapper[1] + eloValues.getOrDefault(kitType, EloHandler.DEFAULT_ELO);
        });

        return wrapper[1] / wrapper[0];
    }

    public void setElo(Set<UUID> playerUuids, KitType kitType, int newElo) {
        Map<KitType, Integer> partyElo = eloData.computeIfAbsent(playerUuids, i -> new ConcurrentHashMap<>());
        partyElo.put(kitType, newElo);

        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            try {
                eloRepository.saveElo(playerUuids, partyElo);
            } catch (IOException ex) {
                // just log, nothing else to do.
                ex.printStackTrace();
            }
        });
    }

    public void loadElo(Set<UUID> playerUuids) {
        Map<KitType, Integer> partyElo;

        try {
            partyElo = new ConcurrentHashMap<>(eloRepository.loadElo(playerUuids));
        } catch (IOException ex) {
            // just print + return an empty map, this will cause us
            // to fall back to default values.
            ex.printStackTrace();
            partyElo = new ConcurrentHashMap<>();
        }

        eloData.put(playerUuids, partyElo);
    }

    public void unloadElo(Set<UUID> playerUuids) {
        eloData.remove(playerUuids);
    }

    public Map<String, Integer> topElo(KitType type) {
        Map<String, Integer> topElo;

        try {
            topElo = eloRepository.topElo(type);
        } catch (IOException ex) {
            ex.printStackTrace();
            topElo = ImmutableMap.of();
        }

        return topElo;
    }

    public void resetElo(final UUID player) {
        Bukkit.getLogger().info("Resetting elo of " + PotPvPSI.getInstance().getUuidCache().name(player) + ".");
        Bukkit.getScheduler().runTaskAsynchronously(PotPvPSI.getInstance(), () -> {
            unloadElo(ImmutableSet.of(player));
            try {
                eloRepository.saveElo(ImmutableSet.of(player), ImmutableMap.of());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}