package net.frozenorb.potpvp.kt.morpheus.game.parameter

interface GameParameter {

    fun getDisplayName(): String
    fun getOptions(): List<GameParameterOption>

}