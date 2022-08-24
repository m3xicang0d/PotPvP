package net.frozenorb.potpvp.game.morpheus;

import net.frozenorb.potpvp.PotPvPSI;
import net.frozenorb.potpvp.game.morpheus.menu.EventsMenu;
import net.frozenorb.potpvp.kt.morpheus.game.Game;
import net.frozenorb.potpvp.kt.morpheus.game.GameState;
import net.frozenorb.potpvp.kt.morpheus.game.bukkit.event.GameStateChangeEvent;
import net.frozenorb.potpvp.kt.morpheus.game.bukkit.event.PlayerGameInteractionEvent;
import net.frozenorb.potpvp.kt.morpheus.game.bukkit.event.PlayerJoinGameEvent;
import net.frozenorb.potpvp.kt.morpheus.game.bukkit.event.PlayerQuitGameEvent;
import net.frozenorb.potpvp.util.VisibilityUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventListeners implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && event.getItem().equals(EventItems.getEventItem()) && PotPvPSI.getInstance().getLobbyHandler().isInLobby(player)) {
            /*if (GameQueue.INSTANCE.getCurrentGames().size() == 1) {
                Game game = GameQueue.INSTANCE.getCurrentGames().get(0);
                if (game.getState() == GameState.STARTING) {
                    if (game.getMaxPlayers() > 0 && game.getPlayers().size() >= game.getMaxPlayers()) {
                        player.sendMessage(ChatColor.RED + "This event is currently full! Sorry!");
                        return;
                    }
                    game.add(player);
                } else {
                    game.addSpectator(player);
                }
                return;
            }*/
            new EventsMenu().openMenu(player);
        }

    }

    @EventHandler
    public void onGameStateChangeEvent(GameStateChangeEvent event) {
        Game game = event.getGame();

        if (event.getTo() == GameState.ENDED) {
            PotPvPSI.getInstance().getArenaHandler().releaseArena(game.getArena());
            for (Player player : game.getPlayers()) {
                if (player != null && player.isOnline()) {
                    //Nametag PotPvPSI.getInstance().nameTagHandler.reloadPlayer(player);
                    //Nametag PotPvPSI.getInstance().nameTagHandler.reloadOthersFor(player);
                    VisibilityUtils.updateVisibility(player);
                    PotPvPSI.getInstance().getLobbyHandler().returnToLobby(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoinGameEvent(PlayerJoinGameEvent event) {
        //Nametag PotPvPSI.getInstance().nameTagHandler.reloadPlayer(event.getPlayer());
        //Nametag PotPvPSI.getInstance().nameTagHandler.reloadOthersFor(event.getPlayer());
        for (Player player : event.getGame().getPlayers()) {
            VisibilityUtils.updateVisibility(player);
        }
    }

    @EventHandler
    public void onPlayerQuitGameEvent(PlayerQuitGameEvent event) {
        //Nametag PotPvPSI.getInstance().nameTagHandler.reloadPlayer(event.getPlayer());
        //Nametag PotPvPSI.getInstance().nameTagHandler.reloadOthersFor(event.getPlayer());
    }

    @EventHandler
    public void onPlayerGameInteractionEvent(PlayerGameInteractionEvent event) {
        //Nametag PotPvPSI.getInstance().nameTagHandler.reloadPlayer(event.getPlayer());
        //Nametag PotPvPSI.getInstance().nameTagHandler.reloadOthersFor(event.getPlayer());
        VisibilityUtils.updateVisibility(event.getPlayer());
    }

}
