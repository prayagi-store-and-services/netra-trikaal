package com.prayagi.netratrikaal

import com.prayagi.trikaal.*
import java.time.Instant
import org.junit.Assert.*
import org.junit.Test

class PeriodOutlookTest {
    private val t0 = Instant.parse("2026-10-08T03:30:00Z")
    @Test fun houseCountsFromTheMoonSignInclusive() {
        assertEquals(1, PeriodOutlook.house(0, 0)); assertEquals(2, PeriodOutlook.house(1, 0)); assertEquals(12, PeriodOutlook.house(11, 0)); assertEquals(5, PeriodOutlook.house(0, 8))
    }
    @Test fun favourableUsesTheDailyTable() {
        assertTrue(PeriodOutlook.isFavourable(Graha.SATURN, 3)); assertFalse(PeriodOutlook.isFavourable(Graha.SATURN, 8)); assertTrue(PeriodOutlook.isFavourable(Graha.JUPITER, 9))
        assertFalse(PeriodOutlook.isFavourable(Graha.RAHU, 1))
    }
    @Test fun buildKeepsOrderAndFlags() {
        val t1 = t0.plusSeconds(86400)
        val s = PeriodOutlook.build(Graha.SUN, 0, 5, t0, listOf(t1 to 6))
        assertEquals(2, s.size); assertEquals(6, s[0].house); assertTrue(s[0].favourable); assertEquals(7, s[1].house); assertFalse(s[1].favourable); assertEquals(t1, s[1].from)
    }
    @Test fun longPeriodsLeaveOutQuickPlanets() {
        assertTrue(PeriodOutlook.planetsFor(7).contains(Graha.MOON))
        assertFalse(PeriodOutlook.planetsFor(30).contains(Graha.MOON)); assertTrue(PeriodOutlook.planetsFor(30).contains(Graha.SUN))
        assertEquals(listOf(Graha.MARS, Graha.JUPITER, Graha.SATURN), PeriodOutlook.planetsFor(365))
    }
    @Test fun engineWeekForTheMoonHasSeveralSignChanges() {
        val s = PeriodOutlook.compute(Graha.MOON, 0, t0, 7)!!
        assertTrue("segments ${s.size}", s.size in 3..5)
        for (i in 1 until s.size) assertTrue(s[i].from > s[i - 1].from)
    }
    @Test fun engineYearForSaturnStartsWithItsRealHouse() {
        val r = TrikaalIngress.rashiOf(Graha.SATURN, t0)!!
        val s = PeriodOutlook.compute(Graha.SATURN, 3, t0, 365)!!
        assertEquals(PeriodOutlook.house(r, 3), s[0].house)
    }
}
