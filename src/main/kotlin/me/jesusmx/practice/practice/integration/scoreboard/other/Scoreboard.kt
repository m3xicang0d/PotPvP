package me.jesusmx.practice.practice.integration.scoreboard.other

import org.bukkit.entity.Player
import java.util.function.BiConsumer

abstract class Scoreboard : BiConsumer<Player, MutableList<String>>