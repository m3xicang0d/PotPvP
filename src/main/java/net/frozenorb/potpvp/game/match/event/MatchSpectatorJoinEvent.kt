package net.frozenorb.potpvp.game.match.event

import net.frozenorb.potpvp.game.match.Match
import org.bukkit.entity.Player

class MatchSpectatorJoinEvent(val spectator: Player, match: Match) : MatchEvent(match)