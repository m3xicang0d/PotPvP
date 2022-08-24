package net.frozenorb.potpvp.kt.morpheus

import net.frozenorb.potpvp.kt.morpheus.game.GameQueue
import org.bukkit.plugin.java.JavaPlugin

class Morpheus(val plugin: JavaPlugin) {

    init {
        GameQueue.run(plugin)
    }

}