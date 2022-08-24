package net.frozenorb.potpvp.game.kittype.menu.manage;

import com.google.common.collect.ImmutableList;
import net.frozenorb.potpvp.commands.highstaff.manage.ManageCommand;
import net.frozenorb.potpvp.game.kittype.HealingMethod;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.BooleanTraitButton;
import net.frozenorb.potpvp.util.menu.Menu;
import net.frozenorb.potpvp.util.menu.MenuBackButton;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mazen Kotb
 */
public class ManageKitTypeMenu extends Menu {

    public final KitType type;

    @Override
    public String getTitle(Player player) {
        return ("Editing " + type.getDisplayName());
    }

    public ManageKitTypeMenu(KitType type) {
        setNoncancellingInventory(true);
        setUpdateAfterClick(false);
        this.type = type;
    }

    @Override
    public int size(Player player) {
        return 9 * 6;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(getSlot(1, 1), new BooleanTraitButton<>(type, "Hidden", KitType::setHidden, KitType::isHidden, KitType::saveAsync));
        buttons.put(getSlot(2, 1), new BooleanTraitButton<>(type, "Editor Item Spawn", KitType::setEditorSpawnAllowed, KitType::isEditorSpawnAllowed, KitType::saveAsync));
        buttons.put(getSlot(3, 1), new BooleanTraitButton<>(type, "Health Shown", KitType::setHealthShown, KitType::isHealthShown, KitType::saveAsync));
        buttons.put(getSlot(4, 1), new BooleanTraitButton<>(type, "Building Allowed", KitType::setBuildingAllowed, KitType::isBuildingAllowed, KitType::saveAsync));
        buttons.put(getSlot(5, 1), new BooleanTraitButton<>(type, "Hardcore Healing", KitType::setHardcoreHealing, KitType::isHardcoreHealing, KitType::saveAsync));
        buttons.put(getSlot(6, 1), new BooleanTraitButton<>(type, "Pearl Damage", KitType::setPearlDamage, KitType::isPearlDamage, KitType::saveAsync));
        buttons.put(getSlot(7, 1), new BooleanTraitButton<>(type, "Supports Ranked", KitType::setSupportsRanked, KitType::isSupportsRanked, KitType::saveAsync));
        buttons.put(getSlot(2, 2), new BooleanTraitButton<>(type, "Allow Absorption", KitType::setAllowAbsorption, KitType::isAllowAbsorption, KitType::saveAsync));
        buttons.put(getSlot(1, 3), new BooleanTraitButton<>(type, "Spawn with Horses", KitType::setSpawnHorses, KitType::isSpawnHorses, KitType::saveAsync));
        buttons.put(getSlot(2, 3), new BooleanTraitButton<>(type, "Food Level Change", KitType::setFoodLevelChange, KitType::isFoodLevelChange, KitType::saveAsync));

        buttons.put(getSlot(1, 2), new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED.toString() + "Soup Healing";
            }

            @Override
            public Material getMaterial(Player player) {
                return type.getHealingMethod() == HealingMethod.SOUP ? Material.REDSTONE_TORCH_ON : Material.LEVER;
            }

            @Override
            public List<String> getDescription(Player player) {
                return ImmutableList.of(
                    ChatColor.YELLOW + "Current: " + ChatColor.WHITE + (type.getHealingMethod() == HealingMethod.SOUP ? "On" : "Off"),
                    "",
                    ChatColor.GREEN.toString() + ChatColor.BOLD + "Click to toggle"
                );
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                HealingMethod healingMethod = type.getHealingMethod();

                if (healingMethod == HealingMethod.SOUP) {
                    type.setHealingMethod(HealingMethod.POTIONS);
                } else {
                    type.setHealingMethod(HealingMethod.SOUP);
                }
                type.saveAsync();
            }

        });

        buttons.put(getSlot(4, 0), new Button() {

            @Override
            public String getName(Player player) {
                return ChatColor.RED.toString() + ChatColor.BOLD + "Wipe existing kits";
            }

            @Override
            public List<String> getDescription(Player player) {
                return ImmutableList.of(
                    "",
                    ChatColor.RED + "Removes all saved " + type.getDisplayName() + " kits",
                    ChatColor.RED + "(includes online and offline players)",
                    "",
                    ChatColor.RED + "For safety reasons this button is disabled,",
                    ChatColor.RED + "use /kit wipekits type " + type.getId().toLowerCase()
                );
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.TNT;
            }

        });

        buttons.put(getSlot(3, 0), new SaveKitTypeButton(type));
        buttons.put(getSlot(5, 0), new CancelKitTypeEditButton());
        buttons.put(getSlot(8, 0), new MenuBackButton(p -> new ManageCommand.ManageMenu().openMenu(p)));
        ItemStack[] kit = type.getEditorItems();
        int x = 0;
        int y = 2;

        for (ItemStack editorItem : kit) {
            if (editorItem != null) {
                if (editorItem.getType() != Material.AIR) {
                    buttons.put(getSlot(x, y), nonCancellingItem(editorItem));
                }
            }

            x++;

            if (x >= 9) {
                x = 0;
                y++;
                if (y >= 6) {
                    break;
                }
            }
        }

        return buttons;
    }


    public boolean isSoup(HealingMethod healingMethod) {
        return healingMethod == HealingMethod.SOUP;
    }

    public Button nonCancellingItem(ItemStack stack) {
        return new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return stack;
            }

            @Override
            public String getName(Player player) {
                return stack.getItemMeta().getDisplayName();
            }

            @Override
            public List<String> getDescription(Player player) {
                return stack.getItemMeta().getLore();
            }

            @Override
            public Material getMaterial(Player player) {
                return stack.getType();
            }

            @Override
            public boolean shouldCancel(Player player, int slot, ClickType clickType) {
                return false;
            }
        };
    }
}
