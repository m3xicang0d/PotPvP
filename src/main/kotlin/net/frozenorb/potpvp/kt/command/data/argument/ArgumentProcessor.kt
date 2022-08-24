package net.frozenorb.potpvp.kt.command.data.argument

import net.frozenorb.potpvp.kt.command.data.flag.Flag
import net.frozenorb.potpvp.kt.command.data.processor.Processor
import java.util.ArrayList

class ArgumentProcessor : Processor<Array<String>, Arguments> {

    override fun process(type: Array<String>): Arguments {
        val flags = ArrayList<String>()
        val arguments = ArrayList<String>()

        for (s in type) {
            if (s.isNotEmpty()) {
                if (s[0] == '-' && s != "-" && matches(s)) {
                    val flag = getFlagName(s)
                    if (flag != null) {
                        flags.add(flag)
                    }
                } else {
                    arguments.add(s)
                }
            }
        }

        return Arguments(arguments, flags)
    }

    public fun getFlagName(flag: String): String? {
        val matcher = Flag.FLAG_PATTERN.matcher(flag)

        if (matcher.matches()) {
            val name = matcher.replaceAll("$2$3")
            return if (name.length == 1) name else name.toLowerCase()
        }

        return null
    }

    public fun matches(flag: String): Boolean {
        return Flag.FLAG_PATTERN.matcher(flag).matches()
    }

}