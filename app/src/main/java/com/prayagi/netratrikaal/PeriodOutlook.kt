package com.prayagi.netratrikaal

import com.prayagi.trikaal.*
import java.time.Instant

/**
 * Weekly, monthly and yearly horoscope as the traditional gochara rule: where each planet is, counted in houses from your Moon sign,
 * and the date it moves to the next sign. A house is "favourable" or not by the same table as the daily reading (Rashifal.FAVOURABLE_FROM_MOON).
 * It shows where the planets are going to be. It does not rate your week and gives no percentages.
 */
object PeriodOutlook {
    data class Segment(val house: Int, val favourable: Boolean, val from: Instant)

    fun house(planetRashi: Int, moonRashi: Int): Int = (planetRashi - moonRashi + 12) % 12 + 1
    fun isFavourable(g: Graha, house: Int): Boolean = Rashifal.FAVOURABLE_FROM_MOON[g]?.contains(house) == true

    /** Pure part: the planet's sign at the start plus its later sign changes (time, new sign), in order. */
    fun build(g: Graha, moonRashi: Int, startRashi: Int, start: Instant, changes: List<Pair<Instant, Int>>): List<Segment> {
        val out = ArrayList<Segment>()
        out += Segment(house(startRashi, moonRashi), isFavourable(g, house(startRashi, moonRashi)), start)
        for ((t, r) in changes) { val h = house(r, moonRashi); out += Segment(h, isFavourable(g, h), t) }
        return out
    }

    /** Planets worth listing for a period: the quick ones are left out of long periods because they change sign too often to read. */
    fun planetsFor(days: Int): List<Graha> = when {
        days <= 7 -> listOf(Graha.MOON, Graha.SUN, Graha.MARS, Graha.MERCURY, Graha.VENUS, Graha.JUPITER, Graha.SATURN)
        days <= 31 -> listOf(Graha.SUN, Graha.MARS, Graha.MERCURY, Graha.VENUS, Graha.JUPITER, Graha.SATURN)
        else -> listOf(Graha.MARS, Graha.JUPITER, Graha.SATURN)
    }

    fun compute(g: Graha, moonRashi: Int, start: Instant, days: Int): List<Segment>? {
        val r0 = TrikaalIngress.rashiOf(g, start) ?: return null
        val end = start.plusSeconds(days * 86400L)
        val changes = ArrayList<Pair<Instant, Int>>()
        var t = start
        while (changes.size < 8) {
            val left = ((end.epochSecond - t.epochSecond) / 86400).toInt() + 1
            val ch = TrikaalIngress.next(g, t, left) ?: break
            if (ch.at >= end) break
            changes += ch.at to ch.to
            t = ch.at.plusSeconds(60)
        }
        return build(g, moonRashi, r0, start, changes)
    }
}
