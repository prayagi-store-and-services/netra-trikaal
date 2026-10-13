package com.prayagi.netratrikaal

import org.junit.Assert.*
import org.junit.Test

class GemstonesTest {
    @Test fun nineGrahasEachWithOneGem() {
        assertEquals(9, Gemstones.TABLE.size); assertEquals(9, Gemstones.TABLE.map { it.planet }.toSet().size); assertEquals(9, Gemstones.TABLE.map { it.gem }.toSet().size)
    }
    @Test fun knownPairs() {
        assertEquals("Ruby", Gemstones.gemFor("sun")?.gem); assertEquals("Blue Sapphire", Gemstones.gemFor("Saturn")?.gem); assertEquals("Cat's Eye", Gemstones.gemFor("Ketu")?.gem)
        assertNull(Gemstones.gemFor("Pluto"))
    }
}
