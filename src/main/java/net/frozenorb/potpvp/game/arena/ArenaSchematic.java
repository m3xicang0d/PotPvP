package net.frozenorb.potpvp.game.arena;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.Vector;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.potpvp.kt.morpheus.game.event.GameEvent;
import org.bukkit.Material;

import java.io.File;

public final class ArenaSchematic {

    /**
     * Name of this schematic (ex "Candyland")
     */
    @Getter public String name;

    /**
     * If matches can be scheduled on an instance of this arena.
     * Only impacts match scheduling, admin commands are (ignoring visual differences) nonchanged
     */
    @Setter public boolean enabled = false;

    /**
     * Maximum number of players that can occupy an instance of this arena.
     * Some small schematics should only be used for smaller fights
     */
    @Getter @Setter public int maxPlayerCount = 256;

    /**
     * Minimum number of players that can occupy an instance of this arena.
     * Some large schematics should only be used for larger fights
     */
    @Getter @Setter public int minPlayerCount = 2;

    /**
     * If this schematic can be used for ranked matches
     * Some "joke" schematics cannot be used for ranked (due to their nature)
     */
    @Getter @Setter public boolean supportsRanked = false;

    /**
     * If this schematic can be only be used for archer matches
     * Some schematics are built for specifically archer fights
     */
    @Getter @Setter public boolean archerOnly = false;

    /**
     * If this schematic can be only be used for archer matches
     * Some schematics are built for specifically archer fights
     */
    @Getter @Setter public boolean teamFightsOnly = false;

    /**
     * If this schematic can be only be used for Sumo matches
     * Some schematics are built for specifically Sumo fights
     */
    @Getter @Setter public boolean sumoOnly = false;

    /**
     * If this schematic can be only be used for Sumo matches
     * Some schematics are built for specifically Skywars fights
     */
    @Getter @Setter public boolean skywarsOnly = false;

    /**
     * If this schematic can be only be used for Bridges matches
     * Some schematics are built for specifically Bridges fights
     */
    @Getter @Setter public boolean bridgesOnly = false;

    /**
     * If this schematic can be only be used for PearlFight matches
     * Some schematics are built for specifically PearlFight fights
     */
    @Getter @Setter public boolean pearlFightOnly = false;

    /**
     * If this schematic can be only be used for Spleef matches
     * Some schematics are built for specifically Spleef fights
     */
    @Getter @Setter public boolean spleefOnly = false;

    /**
     * If this schematic can be only be used for BuildUHC matches
     * Some schematics are built for specifically BuildUHC fights
     */
    @Getter @Setter public boolean isGameMapOnly = false;
    @Getter @Setter public boolean buildUHCOnly = false;

    @Getter @Setter public boolean HCFOnly = false;
    @Getter @Setter public boolean allowPearls = true;
    @Getter @Setter public boolean raidingOnly = false;

    @Getter @Setter public String eventName = null;

    @Getter @Setter public Material arenaItem = Material.MAP;

    /**
     * Index on the X axis on the grid (and in calculations regarding model arenas)
     * @see ArenaGrid
     */
    @Getter @Setter public int gridIndex;

    public ArenaSchematic() {} // for gson

    public ArenaSchematic(String name) {
        this.name = Preconditions.checkNotNull(name, "name");
    }

    public File getSchematicFile() {
        return new File(ArenaHandler.WORLD_EDIT_SCHEMATICS_FOLDER, name + ".schematic");
    }

    public Vector getModelArenaLocation() {
        int xModifier = ArenaGrid.GRID_SPACING_X * gridIndex;

        return new Vector(
                ArenaGrid.STARTING_POINT.getBlockX() - xModifier,
                ArenaGrid.STARTING_POINT.getBlockY(),
                ArenaGrid.STARTING_POINT.getBlockZ()
        );
    }

    public void pasteModelArena() throws Exception {
        Vector start = getModelArenaLocation();
        WorldEditUtils.paste(this, start);
    }

    public void removeModelArena() throws Exception {
        Vector start = getModelArenaLocation();
        Vector size = WorldEditUtils.readSchematicSize(this);

        WorldEditUtils.clear(
                start,
                start.add(size)
        );
    }

    public GameEvent getEvent() {
        if (eventName != null) {
            for (GameEvent event : GameEvent.getEvents()) {
                if (event.getName().equalsIgnoreCase(eventName)) {
                    return event;
                }
            }

            eventName = null;
        }

        return null;
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof ArenaSchematic && ((ArenaSchematic) o).name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public boolean isEnabled() {
        return enabled;
    }
}