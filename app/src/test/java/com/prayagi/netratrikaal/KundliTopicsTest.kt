package com.prayagi.netratrikaal

import com.prayagi.trikaal.Graha
import com.prayagi.trikaal.Period
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class KundliTopicsTest {
    @Test fun navamshaOfAriesStartIsAries() = assertEquals(0.0, navamshaLongitude(0.0), 1e-9)
    @Test fun navamshaOfFourthPartOfAriesIsCancer() = assertEquals(3, (navamshaLongitude(10.5) / 30).toInt())
    @Test fun navamshaOfLastPartOfPiscesIsPisces() = assertEquals(11, (navamshaLongitude(359.9) / 30).toInt())
    @Test fun subPeriodsCoverTheParentExactlyAndStartWithItsLord() {
        val p = Period(Graha.MOON, Instant.parse("2021-11-09T00:00:00Z"), Instant.parse("2031-11-09T00:00:00Z"))
        val s = subPeriods(p)
        assertEquals(9, s.size)
        assertEquals(Graha.MOON, s[0].lord)
        assertEquals(p.start, s.first().start)
        assertEquals(p.end, s.last().end)
        for (i in 0 until 8) assertEquals(s[i].end, s[i + 1].start)
        assertEquals(Graha.MARS, s[1].lord)
    }
}
