package net.frozenorb.potpvp.kt.command.data.flag

import net.frozenorb.potpvp.kt.command.data.Data

data class FlagData(val names: List<String>,
                    val description: String,
                    val defaultValue: Boolean,
                    val methodIndex: Int) : Data