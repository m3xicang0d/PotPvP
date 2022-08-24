package net.frozenorb.potpvp.integration.holograms;

import lombok.Getter;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.integration.holograms.task.HologramsTask;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import net.frozenorb.potpvp.util.LocationUtils;
import net.frozenorb.potpvp.util.bukkit.files.ConfigFile;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class HologramsHandler {

    public final Map<Integer, Map.Entry<String, Integer>> globalPositions = new HashMap<>();
    public final Map<KitType, Map<Integer, Map.Entry<String, Integer>>> kitsPositions = new HashMap<>();
    public static final List<PracticeHologram> holograms = new ArrayList<>();
    private static final ConfigFile config = PotPvPSI.getInstance().getHologramsConfig();

    public HologramsHandler() {
        new HologramsTask().runTaskTimer(PotPvPSI.getInstance(), 0L, PotPvPSI.getInstance().getHologramsConfig().getInt("HOLOGRAMS.UPDATE-TIME") * 20L);
    }

    public void load() {
        ConfigurationSection section = config.getConfiguration().getConfigurationSection("PLACES");
        if(section == null) return;
        for (String s : section.getKeys(false)) {
            Set<String> hlms = config.getConfiguration().getConfigurationSection("HOLOGRAMS").getKeys(false);
            if (!hlms.contains(s)) {
                System.out.println(s + " Kittype dont not exist more!");
                continue;
            }
            PracticeHologram hologram = new PracticeHologram(s, LocationUtils.deserialize(config.getString("PLACES." + s)));
            hologram.spawn();
            holograms.add(hologram);
        }
    }

    public static void spawn(String hologram, Location location) {
        Set<String> hlms = config.getConfiguration().getConfigurationSection("HOLOGRAMS").getKeys(false);
        if (!hlms.contains(hologram)) {
            System.out.println(hologram + " Kittype dont not exist more!");
            return;
        }
        AtomicBoolean spawned = new AtomicBoolean(false);
        AtomicReference<PracticeHologram> practiceHologram = new AtomicReference<>();
        holograms.forEach(h -> {
            if(h.getKitType().equalsIgnoreCase(hologram)) {
                spawned.set(true);
                practiceHologram.set(h);
            }
        });
        if(spawned.get()) {
            holograms.remove(practiceHologram.get());
            practiceHologram.get().destroy();
        }
        PracticeHologram nH = new PracticeHologram(hologram, location);
        nH.spawn();
        holograms.add(nH);
        config.getConfiguration().set("PLACES." + hologram, LocationUtils.serialize(location));
        config.save();
    }

    @Command(names = "spawnhologram")
    public static void onCommand(Player player, @Param(name = "hologram") String str) {
        spawn(str, player.getLocation());
    }

    public void save() {
        holograms.forEach(hologram -> {
            config.getConfiguration().set("PLACES." + hologram.getKitType(), LocationUtils.serialize(hologram.getLocation()));
            config.save();

        });
    }
}