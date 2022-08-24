package net.frozenorb.potpvp.kt.morpheus.game.event.impl.skywars

import net.frozenorb.potpvp.kt.morpheus.game.Game
import net.frozenorb.potpvp.kt.morpheus.game.event.impl.lms.LastManStandingGameEventLogic
import org.bukkit.Location

class SkywarsGameEventLogic(game: Game) : LastManStandingGameEventLogic(game) {

    val chests = ArrayList<Location>()

}