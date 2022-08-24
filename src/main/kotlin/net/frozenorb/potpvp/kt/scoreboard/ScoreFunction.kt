package net.frozenorb.potpvp.kt.scoreboard

import net.frozenorb.potpvp.kt.util.TimeUtils
import kotlin.math.roundToInt

interface ScoreFunction<T> {

    fun apply(score: T): String

    companion object {
        @JvmStatic
        val TIME_FANCY = object : ScoreFunction<Float> {
            override fun apply(score: Float): String {
                return if (score >= 60.0f) {
                    TimeUtils.formatIntoMMSS(score.toInt())
                } else {
                    ((10.0 * score).roundToInt() / 10.0).toString() + "s"
                }
            }
        }

        @JvmStatic
        val TIME_SIMPLE = object : ScoreFunction<Int> {
            override fun apply(score: Int): String {
                return TimeUtils.formatIntoMMSS(score)
            }
        }
    }

}