package com.prayagi.netratrikaal

import com.prayagi.trikaal.*
import java.time.Duration
import java.time.Instant
import org.junit.Assert.*
import org.junit.Test

class TrikaalIngressTest {
    private val now = Instant.parse("2026-10-08T03:30:00Z")
    @Test fun moonChangesSignWithinAboutThreeDays() {
        val c = TrikaalIngress.next(Graha.MOON, now)!!
        assertEquals((c.from + 1) % 12, c.to)
        assertTrue(Duration.between(now, c.at).toHours() in 0..80)
        val before = (AstroCore.currentPositions(c.at.minusSeconds(120)) as Computation.Available).value.first { it.graha == Graha.MOON }.rashi
        val after = (AstroCore.currentPositions(c.at.plusSeconds(120)) as Computation.Available).value.first { it.graha == Graha.MOON }.rashi
        assertEquals(c.from, before); assertEquals(c.to, after)
    }
    @Test fun sunChangesSignWithinAboutAMonth() {
        val c = TrikaalIngress.next(Graha.SUN, now)!!
        assertTrue(Duration.between(now, c.at).toDays() in 1..32)
    }
}
