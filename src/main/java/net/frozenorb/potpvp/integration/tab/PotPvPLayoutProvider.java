package net.frozenorb.potpvp.integration.tab;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.kt.util.PlayerUtils;
import net.frozenorb.potpvp.util.tablist.shared.entry.TabElement;
import net.frozenorb.potpvp.util.tablist.shared.entry.TabElementHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.BiConsumer;

public final class PotPvPLayoutProvider implements TabElementHandler {

    static final int MAX_TAB_Y = 20;

    public final BiConsumer<Player, TabElement> lobbyLayoutProvider = new LobbyLayoutProvider();
    public final BiConsumer<Player, TabElement> matchSpectatorLayoutProvider = new MatchSpectatorLayoutProvider();
    public final BiConsumer<Player, TabElement> matchParticipantLayoutProvider = new MatchParticipantLayoutProvider();

    @Override
    public TabElement getElement(Player player) {
        Match match = PotPvPSI.getInstance().getMatchHandler().getMatchPlayingOrSpectating(player);
        TabElement tabElement = new TabElement();

        if (match != null) {
            if (match.isSpectator(player.getUniqueId())) {
                matchSpectatorLayoutProvider.accept(player, tabElement);
            } else {
                matchParticipantLayoutProvider.accept(player, tabElement);
            }
        } else {
            lobbyLayoutProvider.accept(player, tabElement);
        }

        return tabElement;
    }


    static int getPingOrDefault(UUID check) {
        Player player = Bukkit.getPlayer(check);
        return player != null ? PlayerUtils.getPing(player) : 0;
    }

}