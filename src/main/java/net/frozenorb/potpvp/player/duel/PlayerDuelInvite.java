package net.frozenorb.potpvp.player.duel;

import net.frozenorb.potpvp.game.arena.ArenaSchematic;
import net.frozenorb.potpvp.game.kittype.KitType;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class PlayerDuelInvite extends DuelInvite<UUID> {

    private ArenaSchematic schematic;

    public PlayerDuelInvite(Player sender, Player target, KitType kitType) {
        super(sender.getUniqueId(), target.getUniqueId(), kitType);
    }

    public PlayerDuelInvite(Player sender, Player target, KitType kitType, ArenaSchematic schematic) {
        super(sender.getUniqueId(), target.getUniqueId(), kitType);
        this.schematic = schematic;
    }

    public ArenaSchematic getSchematic() {
        return schematic;
    }
}

