package net.frozenorb.potpvp.kt.util.classes

import net.frozenorb.potpvp.integration.spigot.chunk.impl.VortexSpigotChunk
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.jar.JarFile

object ClassUtils {
    @JvmStatic
    fun getClassesInPackage(plugin: Plugin, pkg: String): Set<Class<*>> {
        val classes: MutableSet<Class<*>> = HashSet()
        val pluginMainClass = Class.forName(plugin.description.main)
        val file = File(pluginMainClass.protectionDomain.codeSource.location.toURI())
        JarFile(file).use { jarFile ->
            val e = jarFile.entries()
            while (e.hasMoreElements()) {
                val jarEntry = e.nextElement()
                if (jarEntry.name.endsWith(".class")) {
                    val className = jarEntry.name
                        .replace("/", ".")
                        .replace(".class", "")

                    if(className.startsWith("net.frozenorb.potpvp.game.util.tablist.versions.v1_7_R4")) continue
                    if(className.startsWith("net.frozenorb.potpvp.game.util.tablist.versions.v1_8_R1")) continue
                    if(className.startsWith("net.frozenorb.potpvp.game.util.tablist.versions.v1_8_R3")) continue

                    if(className.startsWith("org.bukkit.entity.player.PlayerHealthChangeEvent")) continue

/*                    if(className.startsWith(CarbonSpigotChunk::class.java.name)) continue
                    if(className.startsWith(cSpigotChunk::class.java.name)) continue*/
                    if(className.startsWith(VortexSpigotChunk::class.java.name)) continue
                    if (className.startsWith(pluginMainClass.getPackage().name)) {
                        try {
                            val clazz = Class.forName(className)
                            if (className.startsWith(pkg)) {
                                classes.add(clazz)
                            }
                        } catch (ignored: ClassNotFoundException) {
                        }
                    }
                }
            }
        }
        return classes
    }

    @JvmStatic
    fun<T> getClassesInPackage(plugin: Plugin, pkg: String, type : Class<T>): Set<Class<T>> {
        val set = getClassesInPackage(plugin, pkg)
        val toReturn = mutableSetOf<Class<T>>()
        for(c in set) {
            try {
                c.asSubclass(type)
                toReturn.add(c as Class<T>)
            } catch (ignored : Exception) {}
        }
        return toReturn
    }

}