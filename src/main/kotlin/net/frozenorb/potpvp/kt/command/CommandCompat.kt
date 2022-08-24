package net.frozenorb.potpvp.kt.command

object CommandCompat {

    public val SPIGOT_CONFIG_CLASS = Class.forName("org.spigotmc.SpigotConfig")
    public val UNKNOWN_COMMAND_MESSAGE = SPIGOT_CONFIG_CLASS.getDeclaredField("unknownCommandMessage")

    init {
        UNKNOWN_COMMAND_MESSAGE.isAccessible = true
    }

    fun getUnknownCommandMessage(): String {
        return UNKNOWN_COMMAND_MESSAGE.get(null) as String
    }

}