package com.prayagi.netratrikaal

import com.prayagi.trikaal.Graha
import org.junit.Assert.assertEquals
import org.junit.Test

class KundliSummaryTest {
    @Test fun fullSummaryHasFiveLines() {
        val l = kundliSummaryHi(4, Graha.SUN, 0, 1, 2, 9, Graha.VENUS, Graha.MOON, "2031-05-01", listOf(Graha.SATURN))
        assertEquals(5, l.size)
        assertEquals("आपका लग्न सिंह है। इसके स्वामी सूर्य हैं।", l[0])
        assertEquals("आपकी चंद्र राशि मेष है। जन्म नक्षत्र भरणी (चरण 2) है।", l[1])
        assertEquals("अभी शुक्र की महादशा और चंद्र की अंतर्दशा चल रही है। यह महादशा 2031-05-01 तक है।", l[3])
        assertEquals("जन्म के समय वक्री ग्रह: शनि।", l[4])
    }
    @Test fun missingDashaLineIsLeftOut() {
        val l = kundliSummaryHi(0, Graha.MARS, 3, 0, 1, 1, null, null, null, emptyList())
        assertEquals(4, l.size)
        assertEquals("जन्म के समय कोई मुख्य ग्रह वक्री नहीं था।", l[3])
    }
}
