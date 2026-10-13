package com.prayagi.netratrikaal

import com.prayagi.trikaal.*
import java.time.Instant

/** When a planet next changes sign (sidereal, Lahiri). A retrograde planet can step back, so this is "next sign change", not "final entry". */
object TrikaalIngress {
    data class Change(val graha: Graha, val from: Int, val to: Int, val at: Instant)

    private fun rashiOf(g: Graha, t: Instant): Int? =
        (AstroCore.currentPositions(t) as? Computation.Available)?.value?.firstOrNull { it.graha == g }?.rashi

    fun next(g: Graha, from: Instant, horizonDays: Int = 400): Change? {
        val r0 = rashiOf(g, from) ?: return null
        val step = if (g == Graha.MOON) 3600L else 6 * 3600L
        var lo = from; var t = from.plusSeconds(step)
        val end = from.plusSeconds(horizonDays * 86400L)
        while (t <= end) {
            val r = rashiOf(g, t) ?: return null
            if (r != r0) {
                var a = lo; var b = t
                while (b.epochSecond - a.epochSecond > 60) {
                    val mid = Instant.ofEpochSecond((a.epochSecond + b.epochSecond) / 2)
                    if ((rashiOf(g, mid) ?: return null) == r0) a = mid else b = mid
                }
                return Change(g, r0, rashiOf(g, b) ?: r, b)
            }
            lo = t; t = t.plusSeconds(step)
        }
        return null
    }
}
