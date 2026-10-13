package com.prayagi.trikaal
import java.time.*
import org.junit.Assert.*
import org.junit.Test
class YogaKaranaTest {
    private val ist = ZoneId.of("Asia/Kolkata")
    private val now = ZonedDateTime.of(2026, 10, 8, 9, 0, 0, 0, ist).toInstant()
    @Test fun yogaSpanContainsNowAndIsContiguous() {
        val y = Panchang.yogaAt(now)!!
        assertTrue(y.contains(now))
        assertTrue(Panchang.YOGA_NAMES.contains(y.name))
        val h = Duration.between(y.start, y.end).toHours()
        assertTrue("yoga length $h h", h in 14..30)
        val next = Panchang.yogaAt(y.end.plusSeconds(5))!!
        assertEquals(y.end, next.start)
        assertNotEquals(y.name, next.name)
    }
    @Test fun karanaSpanContainsNowAndIsAboutHalfATithi() {
        val k = Panchang.karanaAt(now)!!
        assertTrue(k.contains(now))
        val h = Duration.between(k.start, k.end).toHours()
        assertTrue("karana length $h h", h in 5..8)
        val next = Panchang.karanaAt(k.end.plusSeconds(5))!!
        assertEquals(k.end, next.start)
    }
    @Test fun karanaNamesFollowTheStandardCycle() {
        assertEquals("Kimstughna", Panchang.karanaName(0))
        assertEquals("Bava", Panchang.karanaName(1))
        assertEquals("Vishti", Panchang.karanaName(7))
        assertEquals("Bava", Panchang.karanaName(8))
        assertEquals("Shakuni", Panchang.karanaName(57))
        assertEquals("Chatushpada", Panchang.karanaName(58))
        assertEquals("Naga", Panchang.karanaName(59))
        assertEquals(27, Panchang.YOGA_NAMES.size)
        assertEquals("Siddhi", Panchang.YOGA_NAMES[15])
    }
    @Test fun tithiAndKaranaAgree() {
        val tn = Panchang.tithiNumberAt(now)!!; val kn = Panchang.karanaNumberAt(now)!!
        assertEquals(tn - 1, kn / 2)
    }
}
