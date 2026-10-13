package com.prayagi.netratrikaal

import org.junit.Assert.*
import org.junit.Test

class VastuTest {
    @Test fun eightDirectionsAndCentre() {
        assertEquals(9, Vastu.TABLE.size); assertEquals(9, Vastu.TABLE.map { it.name }.toSet().size)
        assertEquals("Agni", Vastu.TABLE.first { it.name == "South-East" }.deity); assertEquals("Fire", Vastu.TABLE.first { it.name == "South-East" }.element)
        assertEquals("Kubera", Vastu.TABLE.first { it.name == "North" }.deity)
    }
}
