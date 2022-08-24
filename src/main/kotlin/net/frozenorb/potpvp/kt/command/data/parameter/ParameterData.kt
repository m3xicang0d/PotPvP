package net.frozenorb.potpvp.kt.command.data.parameter

import net.frozenorb.potpvp.kt.command.data.Data

data class ParameterData(val name: String,
                         val defaultValue: String,
                         val type: Class<*>,
                         val wildcard: Boolean,
                         val methodIndex: Int,
                         val tabCompleteFlags: Set<String>,
                         val parameterType: Class<*>?) : Data