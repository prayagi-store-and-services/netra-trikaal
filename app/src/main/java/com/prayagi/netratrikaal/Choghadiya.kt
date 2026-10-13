package com.prayagi.netratrikaal

import java.time.DayOfWeek
import java.time.Instant

/**
 * Choghadiya: the daytime and the night are each split into 8 equal parts. The names follow a fixed cycle
 * (Udveg, Chal, Labh, Amrit, Kaal, Shubh, Rog). The first day part is the weekday lord's own name, and the first night part
 * starts five names later in the same cycle. This is a traditional muhurat method, not a prediction.
 */
object Choghadiya {
    enum class Quality { GOOD, NEUTRAL, AVOID }
    data class Part(val name: String, val start: Instant, val end: Instant, val night: Boolean) {
        val quality: Quality get() = when (name) { "Amrit", "Shubh", "Labh" -> Quality.GOOD; "Chal" -> Quality.NEUTRAL; else -> Quality.AVOID }
    }
    private val CYCLE = listOf("Udveg", "Chal", "Labh", "Amrit", "Kaal", "Shubh", "Rog")
    private fun dayStart(w: DayOfWeek): Int = when (w) {
        DayOfWeek.SUNDAY -> 0      // Udveg
        DayOfWeek.MONDAY -> 3      // Amrit
        DayOfWeek.TUESDAY -> 6     // Rog
        DayOfWeek.WEDNESDAY -> 2   // Labh
        DayOfWeek.THURSDAY -> 5    // Shubh
        DayOfWeek.FRIDAY -> 1      // Chal
        DayOfWeek.SATURDAY -> 4    // Kaal
    }

    fun names(weekday: DayOfWeek, night: Boolean): List<String> {
        val s = (dayStart(weekday) + if (night) 5 else 0) % 7
        return List(8) { CYCLE[(s + it) % 7] }
    }

    /** 16 parts: 8 from sunrise to sunset, then 8 from sunset to the next sunrise. */
    fun parts(weekday: DayOfWeek, sunrise: Instant, sunset: Instant, nextSunrise: Instant): List<Part> {
        fun split(a: Instant, b: Instant, night: Boolean): List<Part> {
            val len = (b.epochSecond - a.epochSecond) / 8.0
            val nm = names(weekday, night)
            return List(8) { i ->
                Part(nm[i], Instant.ofEpochSecond(a.epochSecond + Math.round(len * i)), Instant.ofEpochSecond(a.epochSecond + Math.round(len * (i + 1))), night)
            }
        }
        return split(sunrise, sunset, false) + split(sunset, nextSunrise, true)
    }
}
