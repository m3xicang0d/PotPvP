package net.frozenorb.potpvp.game.arena.menu.manageschematic;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.arena.Arena;
import net.frozenorb.potpvp.game.arena.ArenaHandler;
import net.frozenorb.potpvp.game.arena.ArenaSchematic;
import net.frozenorb.potpvp.util.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

final class SchematicStatusButton extends Button {

    public final ArenaSchematic schematic;

    SchematicStatusButton(ArenaSchematic schematic) {
        this.schematic = Preconditions.checkNotNull(schematic, "schematic");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + schematic.getName() + " Status";
    }

    @Override
    public List<String> getDescription(Player player) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        int totalCopies = 0;
        int inUseCopies = 0;

        for (Arena arena : arenaHandler.getArenas(schematic)) {
            totalCopies++;

            if (arena.isInUse()) {
                inUseCopies++;
            }
        }

        List<String> description = new ArrayList<>();

        description.add("");
        description.add(ChatColor.GREEN + "Copies: " + ChatColor.WHITE + totalCopies);
        description.add(ChatColor.GREEN + "Copies in use: " + ChatColor.WHITE + inUseCopies);

        return description;
    }

    @Override
    public int getAmount(Player player) {
        ArenaHandler arenaHandler = PotPvPSI.getInstance().getArenaHandler();
        return arenaHandler.getArenas(schematic).size();
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.NAME_TAG;
    }

}