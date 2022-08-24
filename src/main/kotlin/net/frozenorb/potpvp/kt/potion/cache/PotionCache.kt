package net.frozenorb.potpvp.kt.potion.cache

import java.util.*
object PotionCache {

    @JvmStatic
    fun init() {
        map = mutableMapOf()
    }
    @JvmStatic
    lateinit var map : MutableMap<UUID, Int>
}