package net.frozenorb.potpvp.kt.nametag

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

class ScoreboardTeamPacketMod(val name: String, val prefix: String, val suffix: String, val players: MutableCollection<String>?, val paramInt: Int) {

    val packet = PacketPlayOutScoreboardTeam()

    init {
        try {
            aField.set(packet, name)
            hField.set(packet, paramInt)
            if(paramInt == 0 || paramInt == 2) {
                bField.set(packet, name)
                cField.set(packet, prefix)
                dField.set(packet, suffix)
                iField.set(packet, 3)
                eField.set(packet, "always")
            }
            if(paramInt == 3 || paramInt == 4) {
                gField.set(packet, players)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /* constructor(name: String, players: MutableCollection<String>, paramInt: Int): this(name, "", "", players, paramInt) {
        try {
            iField.set(packet, 3)
            aField.set(packet, name)
            hField.set(packet, paramInt)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        addAll(players)
    }
*/
    fun sendToPlayer(bukkitPlayer: Player) {
        (bukkitPlayer as CraftPlayer).handle.playerConnection?.sendPacket(packet)
//        .sendPacket(packet)
    }

    companion object {
        val aField = PacketPlayOutScoreboardTeam::class.java.getDeclaredField("a")
        val bField = PacketPlayOutScoreboardTeam::class.java.getDeclaredField("b")
        val cField = PacketPlayOutScoreboardTeam::class.java.getDeclaredField("c")
        val dField = PacketPlayOutScoreboardTeam::class.java.getDeclaredField("d")
        val eField = PacketPlayOutScoreboardTeam::class.java.getDeclaredField("e")
        val gField = PacketPlayOutScoreboardTeam::class.java.getDeclaredField("g")
        val hField = PacketPlayOutScoreboardTeam::class.java.getDeclaredField("h")
        val iField = PacketPlayOutScoreboardTeam::class.java.getDeclaredField("i")

        init {
            aField.isAccessible = true
            bField.isAccessible = true
            cField.isAccessible = true
            dField.isAccessible = true
            eField.isAccessible = true
            gField.isAccessible = true
            hField.isAccessible = true
            iField.isAccessible = true
        }
    }

}