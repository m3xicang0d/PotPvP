package net.frozenorb.potpvp.kt.command.data.argument

class Arguments(val arguments: MutableList<String>, val flags: ArrayList<String>) {

    fun hasFlag(flag: String): Boolean {
        return this.flags.contains(flag.toLowerCase())
    }

    fun join(from: Int, to: Int, delimiter: Char): String {
        var to = to
        if (to > this.arguments.size - 1 || to < 1) {
            to = this.arguments.size - 1
        }

        val builder = StringBuilder()
        for (i in from..to) {
            builder.append(this.arguments[i])
            if (i != to) {
                builder.append(delimiter)
            }
        }

        return builder.toString()
    }

    fun join(from: Int, delimiter: Char): String {
        return this.join(from, -1, delimiter)
    }

    fun join(from: Int): String {
        return this.join(from, ' ')
    }

    fun join(delimiter: Char): String {
        return this.join(0, delimiter)
    }

    fun join(): String {
        return this.join(' ')
    }

}