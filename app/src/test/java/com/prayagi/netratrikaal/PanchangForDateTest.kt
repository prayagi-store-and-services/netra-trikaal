package com.prayagi.netratrikaal

import com.prayagi.trikaal.*
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Test

class PanchangForDateTest {
    @Test fun brahmaMuhurtaIs96To48MinutesBeforeSunrise() {
        val sr = Instant.parse("2026-10-08T00:30:00Z")
        val (s, e) = PanchangForDateCalc.brahma(sr)
        assertEquals(Duration.ofMinutes(96), Duration.between(s, sr))
        assertEquals(Duration.ofMinutes(48), Duration.between(e, sr))
    }
    @Test fun aGivenDateGivesARealPanchang() {
        val r = PanchangForDateCalc.compute(LocalDate.of(2026, 10, 8), PRAYAGRAJ)
        assertTrue(r is Computation.Available)
        val v = (r as Computation.Available).value
        assertNotNull(v.tithi); assertNotNull(v.nakshatra); assertNotNull(v.yoga); assertNotNull(v.karana)
        assertTrue(v.tithi!!.contains(v.sunrise))
        assertTrue(v.sunset > v.sunrise)
    }
    @Test fun nextDayMovesTheTithiForward() {
        val a = (PanchangForDateCalc.compute(LocalDate.of(2026, 10, 8), PRAYAGRAJ) as Computation.Available).value
        val b = (PanchangForDateCalc.compute(LocalDate.of(2026, 10, 9), PRAYAGRAJ) as Computation.Available).value
        assertTrue(b.sunrise > a.sunrise)
        assertTrue(b.tithi!!.number != a.tithi!!.number)
    }
}
