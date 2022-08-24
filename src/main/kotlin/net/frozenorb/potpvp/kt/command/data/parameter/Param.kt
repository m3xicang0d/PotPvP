package net.frozenorb.potpvp.kt.command.data.parameter

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Param(val name: String,
                       val defaultValue: String = "",
                       val tabCompleteFlags: Array<String> = [],
                       val wildcard: Boolean = false)