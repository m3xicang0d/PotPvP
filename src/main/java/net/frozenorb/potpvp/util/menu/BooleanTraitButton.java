package net.frozenorb.potpvp.util.menu;

import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public final class BooleanTraitButton<T> extends Button {

    public final T target;
    public final String trait;
    public final BiConsumer<T, Boolean> writeFunction;
    public final Function<T, Boolean> readFunction;
    public final Consumer<T> saveFunction;

    public BooleanTraitButton(T target, String trait, BiConsumer<T, Boolean> writeFunction, Function<T, Boolean> readFunction) {
        this(target, trait, writeFunction, readFunction, (i) -> {
        });
    }

    public BooleanTraitButton(T target, String trait, BiConsumer<T, Boolean> writeFunction, Function<T, Boolean> readFunction, Consumer<T> saveFunction) {
        this.target = target;
        this.trait = trait;
        this.writeFunction = writeFunction;
        this.readFunction = readFunction;
        this.saveFunction = saveFunction;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Edit " + trait;
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            ChatColor.YELLOW + "Current: " + ChatColor.WHITE + (readFunction.apply(target) ? "On" : "Off"),
            "",
            ChatColor.GREEN.toString() + ChatColor.BOLD + "Click to toggle"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return readFunction.apply(target) ? Material.REDSTONE_TORCH_ON : Material.LEVER;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        boolean current = readFunction.apply(target);

        writeFunction.accept(target, !current);
        saveFunction.accept(target);

        player.updateInventory();

        player.sendMessage(ChatColor.GREEN + "Set " + trait + " trait to " + (current ? "off" : "on"));
    }

}
