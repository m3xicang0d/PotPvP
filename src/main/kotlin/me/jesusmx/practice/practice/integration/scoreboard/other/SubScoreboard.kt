package me.jesusmx.practice.practice.integration.scoreboard.other

import me.jesusmx.practice.consumerapi.TriConsumer
import org.bukkit.entity.Player

abstract class SubScoreboard<T> : TriConsumer<Player, MutableList<String>, T>