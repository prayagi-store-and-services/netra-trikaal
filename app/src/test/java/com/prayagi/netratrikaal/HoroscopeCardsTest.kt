package com.prayagi.netratrikaal

import com.prayagi.trikaal.Graha
import org.junit.Assert.assertEquals
import org.junit.Test

class HoroscopeCardsTest {
    private val all = mapOf(Graha.VENUS to 1, Graha.MOON to 1, Graha.SUN to 3, Graha.SATURN to 3, Graha.MARS to 3, Graha.MERCURY to 2, Graha.JUPITER to 2)
    @Test fun fourCardsWhenAllDataPresent() {
        val c = horoscopeCardsHi(all, 2)
        assertEquals(listOf("love", "work", "energy", "social"), c.map { it.key })
        assertEquals("रिश्तों में खुलकर और प्यार से बात करने के लिए अच्छा दिन है।", c[0].lines.last())
    }
    @Test fun cardWithMissingDataIsHidden() {
        val c = horoscopeCardsHi(all - Graha.MARS, 2)
        assertEquals(listOf("love", "work", "social"), c.map { it.key })
        assertEquals(listOf("love", "work", "social"), horoscopeCardsHi(all, null).map { it.key })
    }
    @Test fun unfavourableGivesCareTone() {
        val c = horoscopeCardsHi(all + (Graha.SUN to 1) + (Graha.SATURN to 1), 1)
        assertEquals("काम में आज जोखिम और बड़े फ़ैसले टालें। ज़रूरी काम ही निपटाएँ।", c[1].lines.last())
    }
}
