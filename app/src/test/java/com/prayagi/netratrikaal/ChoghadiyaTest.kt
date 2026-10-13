package com.prayagi.netratrikaal

import java.time.DayOfWeek
import java.time.Instant
import org.junit.Assert.*
import org.junit.Test

class ChoghadiyaTest {
    @Test fun sundayDayIsTheWellKnownOrder() =
        assertEquals(listOf("Udveg", "Chal", "Labh", "Amrit", "Kaal", "Shubh", "Rog", "Udveg"), Choghadiya.names(DayOfWeek.SUNDAY, false))
    @Test fun tuesdayNightIsTheWellKnownOrder() =
        assertEquals(listOf("Kaal", "Labh", "Udveg", "Shubh", "Amrit", "Chal", "Rog", "Kaal"), Choghadiya.names(DayOfWeek.TUESDAY, true))
    @Test fun mondayDayAndNight() {
        assertEquals(listOf("Amrit", "Kaal", "Shubh", "Rog", "Udveg", "Chal", "Labh", "Amrit"), Choghadiya.names(DayOfWeek.MONDAY, false))
        assertEquals(listOf("Chal", "Rog", "Kaal", "Labh", "Udveg", "Shubh", "Amrit", "Chal"), Choghadiya.names(DayOfWeek.MONDAY, true))
    }
    @Test fun firstNightPartForEveryWeekday() {
        val want = mapOf(DayOfWeek.SUNDAY to "Shubh", DayOfWeek.MONDAY to "Chal", DayOfWeek.TUESDAY to "Kaal", DayOfWeek.WEDNESDAY to "Udveg",
            DayOfWeek.THURSDAY to "Amrit", DayOfWeek.FRIDAY to "Rog", DayOfWeek.SATURDAY to "Labh")
        want.forEach { (d, n) -> assertEquals(d.toString(), n, Choghadiya.names(d, true)[0]) }
    }
    @Test fun sixteenContiguousParts() {
        val sr = Instant.parse("2026-10-08T00:20:00Z"); val ss = Instant.parse("2026-10-08T12:10:00Z"); val nsr = Instant.parse("2026-10-09T00:21:00Z")
        val p = Choghadiya.parts(DayOfWeek.THURSDAY, sr, ss, nsr)
        assertEquals(16, p.size); assertEquals(sr, p.first().start); assertEquals(nsr, p.last().end)
        for (i in 1 until p.size) assertEquals(p[i - 1].end, p[i].start)
        assertFalse(p[7].night); assertTrue(p[8].night)
    }
}
