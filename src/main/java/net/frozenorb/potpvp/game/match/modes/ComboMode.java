package net.frozenorb.potpvp.game.match.modes;

import me.jesusmx.practice.practice.game.modes.PvPMode;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.event.MatchEndEvent;
import net.frozenorb.potpvp.game.match.event.MatchStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemDamageEvent;

import java.util.Locale;
import java.util.Objects;

public class ComboMode extends PvPMode {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onStart(MatchStartEvent event) {
        Match match = event.getMatch();
        if(!match.getKitType().getId().equalsIgnoreCase("combo")) return;
        int noDamageTicks = match.getKitType().getId().toLowerCase(Locale.ROOT).contains("combo") ? 3 : 19;
        match.getTeams().forEach(team -> {
            team.getAllMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> {
            });
            team.getAliveMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> {
                p.setMaximumNoDamageTicks(noDamageTicks);
                if (match.getKitType().getId().toLowerCase(Locale.ROOT).contains("combo")) {
                    /*p.setKnockbackProfile(KnockbackConfig.getKbProfileByName("Combo"));
                    p.*/
                } else {
//                    p.setKnockbackProfile(KnockbackConfig.getKbProfileByName("Default"));
                }
            });
        });
    }

    @EventHandler/*
                player.setKnockbackProfile(KnockbackConfig.getKbProfileByName("Default"));
*/

    public void onEnd(MatchEndEvent event) {
        Match match = event.match;
        if(!match.kitType.id.toLowerCase(Locale.ROOT).contains("combo")) return;
        match.getTeams().forEach(team -> {
            team.getAllMembers().forEach(uuid -> {
                Player player = Bukkit.getPlayer(uuid);
                player.setMaximumNoDamageTicks(19);
            });
        });
    }

    @EventHandler
    public void itemDmg(PlayerItemDamageEvent event) {
        if (!event.getItem().getType().name().contains("SWORD")) {
            if (event.getPlayer().getMaximumNoDamageTicks() == 3) {
                event.setDamage(event.getDamage() + 1);
            }
        }
    }
}
