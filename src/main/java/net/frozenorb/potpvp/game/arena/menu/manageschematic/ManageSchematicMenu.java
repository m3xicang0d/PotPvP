package net.frozenorb.potpvp.game.arena.menu.manageschematic;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.arena.ArenaSchematic;
import net.frozenorb.potpvp.game.arena.menu.manageschematics.ManageSchematicsMenu;
import net.frozenorb.potpvp.util.menu.*;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class ManageSchematicMenu extends Menu {

    public final ArenaSchematic schematic;

    @Override
    public String getTitle(Player player) {
        return ("Manage " + schematic.getName());
    }

    public ManageSchematicMenu(ArenaSchematic schematic) {
        setAutoUpdate(true);
        this.schematic = schematic;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new SchematicStatusButton(schematic));
        buttons.put(1, new ToggleEnabledButton(schematic));
        buttons.put(3, new TeleportToModelButton(schematic));
        buttons.put(4, new SaveModelButton(schematic));

        if (PotPvPSI.getInstance().getArenaHandler().getGrid().isBusy()) {
            Button busyButton = Button.placeholder(Material.WOOL, DyeColor.SILVER.getWoolData(), ChatColor.GRAY.toString() + ChatColor.BOLD + "Grid is busy");
            buttons.put(7, busyButton);
            buttons.put(8, busyButton);
        } else {
            buttons.put(7, new CreateCopiesButton(schematic));
            buttons.put(8, new RemoveCopiesButton(schematic));
        }

        buttons.put(9, new MenuBackButton(p -> new ManageSchematicsMenu().openMenu(p)));

        Consumer<ArenaSchematic> save = schematic -> {
            try {
                PotPvPSI.getInstance().getArenaHandler().saveSchematics();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        buttons.put(18, new IntegerTraitButton<>(schematic, "Max Player Count", ArenaSchematic::setMaxPlayerCount, ArenaSchematic::getMaxPlayerCount, save));
        buttons.put(19, new IntegerTraitButton<>(schematic, "Min Player Count", ArenaSchematic::setMinPlayerCount, ArenaSchematic::getMinPlayerCount, save));
        buttons.put(20, new BooleanTraitButton<>(schematic, "Supports Ranked", ArenaSchematic::setSupportsRanked, ArenaSchematic::isSupportsRanked, save));
        buttons.put(21, new BooleanTraitButton<>(schematic, "Archer Only", ArenaSchematic::setArcherOnly, ArenaSchematic::isArcherOnly, save));
        buttons.put(22, new BooleanTraitButton<>(schematic, "Sumo Only", ArenaSchematic::setSumoOnly, ArenaSchematic::isSumoOnly, save));
        buttons.put(23, new BooleanTraitButton<>(schematic, "Spleef Only", ArenaSchematic::setSpleefOnly, ArenaSchematic::isSpleefOnly, save));
        buttons.put(24, new BooleanTraitButton<>(schematic, "BuildUHC Only", ArenaSchematic::setBuildUHCOnly, ArenaSchematic::isBuildUHCOnly, save));
        buttons.put(25, new BooleanTraitButton<>(schematic, "Team Fights Only", ArenaSchematic::setTeamFightsOnly, ArenaSchematic::isTeamFightsOnly, save));
        buttons.put(26, new BooleanTraitButton<>(schematic, "HCF Only", ArenaSchematic::setHCFOnly, ArenaSchematic::isHCFOnly, save));
        buttons.put(28, new BooleanTraitButton<>(schematic, "Game Map only", ArenaSchematic::setGameMapOnly, ArenaSchematic::isGameMapOnly, save));
        buttons.put(29, new BooleanTraitButton<>(schematic, "Base Raiding Only", ArenaSchematic::setRaidingOnly, ArenaSchematic::isRaidingOnly, save));
        buttons.put(30, new BooleanTraitButton<>(schematic, "SkyWars Only", ArenaSchematic::setSkywarsOnly, ArenaSchematic::isSkywarsOnly, save));
        buttons.put(31, new BooleanTraitButton<>(schematic, "Bridges Only", ArenaSchematic::setBridgesOnly, ArenaSchematic::isBridgesOnly, save));
        buttons.put(32, new BooleanTraitButton<>(schematic, "PearlFight Only", ArenaSchematic::setPearlFightOnly, ArenaSchematic::isPearlFightOnly, save));
        buttons.put(33, new BooleanTraitButton<>(schematic, "Allow Enderpearls", ArenaSchematic::setAllowPearls, ArenaSchematic::isAllowPearls, save));

        buttons.put(27, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.GREEN + "Qrakn Game Events";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Collections.singletonList(ChatColor.GRAY + "Manage which events can utilize this arena.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EMERALD;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new ManageEventsMenu(schematic).openMenu(player);
            }
        });

        return buttons;
    }

}