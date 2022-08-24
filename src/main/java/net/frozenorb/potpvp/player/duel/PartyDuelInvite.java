package net.frozenorb.potpvp.player.duel;

import net.frozenorb.potpvp.game.arena.ArenaSchematic;
import net.frozenorb.potpvp.game.kittype.KitType;
import net.frozenorb.potpvp.player.party.Party;

public final class PartyDuelInvite
extends DuelInvite<Party> {
    public PartyDuelInvite(Party sender, Party target, KitType kitTypes) {
        super(sender, target, kitTypes);
    }
    private ArenaSchematic schematic;


    public PartyDuelInvite(Party sender, Party target, KitType kitTypes, ArenaSchematic schematic) {
        super(sender, target, kitTypes);
        this.schematic = schematic;
    }

    public ArenaSchematic getSchematic() {
        return schematic;
    }

}

