package net.frozenorb.potpvp.game.match.modes;

import me.jesusmx.practice.practice.game.modes.PvPMode;
import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.kittype.HealingMethod;
import net.frozenorb.potpvp.game.match.Match;
import net.frozenorb.potpvp.game.match.MatchHandler;
import net.frozenorb.potpvp.kt.morpheus.game.Game;
import net.frozenorb.potpvp.kt.morpheus.game.GameQueue;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

public final class SoupMode extends PvPMode {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.MUSHROOM_SOUP || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Player player = event.getPlayer();
        Match match = matchHandler.getMatchPlaying(player);
        Game game = GameQueue.INSTANCE.getCurrentGame(player);

        if ((game != null && game.getPlayers().contains(player)) || (match != null && match.getKitType().getHealingMethod() == HealingMethod.SOUP && player.getHealth() <= 19)) {
            double current = player.getHealth();
            double max = player.getMaxHealth();

            player.getItemInHand().setType(Material.BOWL);
            player.setHealth(Math.min(max, current + 7D));
        }
    }
/*
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying((Player) event.getEntity());

        if (match != null && match.getKitType().getHealingMethod() == HealingMethod.SOUP) {
            event.setFoodLevel(20);
        }
    }*/

}