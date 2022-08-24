package net.frozenorb.potpvp.kt.command.data.flag

import java.util.regex.Pattern

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Flag(vararg val value: String, val defaultValue: Boolean = false, val description: String = "") {

    companion object {
        val FLAG_PATTERN: Pattern = Pattern.compile("(-)([a-zA-Z])([\\w]*)?")
    }

}