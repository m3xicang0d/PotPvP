package net.frozenorb.potpvp.game.morpheus.menu.parameter;

import lombok.Getter;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.kt.morpheus.game.parameter.GameParameter;
import net.frozenorb.potpvp.kt.morpheus.game.parameter.GameParameterOption;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class HostParameterButton extends Button {

    public final GameParameter parameter;
    @Getter public GameParameterOption selectedOption;

    HostParameterButton(GameParameter parameter) {
        this.parameter = parameter;
        this.selectedOption = parameter.getOptions().get(0);
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        int index = parameter.getOptions().indexOf(selectedOption);

        if (index + 1 == parameter.getOptions().size()) {
            index = 0;
        } else {
            index++;
        }

        selectedOption = parameter.getOptions().get(index);
    }

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_PURPLE + parameter.getDisplayName();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> toReturn = new ArrayList<>();

        for (GameParameterOption option : parameter.getOptions()) {
            if (option.equals(selectedOption)) {
                toReturn.add(ChatColor.GREEN + "» " + ChatColor.GRAY + option.getDisplayName());
            } else {
                toReturn.add(ChatColor.GRAY + option.getDisplayName());
            }
        }

        return toReturn;
    }

    @Override
    public Material getMaterial(Player player) {
        return selectedOption.getIcon().getType();
    }

    @Override
    public int getAmount(Player player) {
        return selectedOption.getIcon().getAmount();
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) selectedOption.getIcon().getDurability();
    }
}
