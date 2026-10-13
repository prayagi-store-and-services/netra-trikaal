package com.prayagi.netratrikaal

import com.prayagi.trikaal.*
import org.junit.Assert.*
import org.junit.Test

class LifeAreasTest {
    private val lords: (Int) -> Graha = { SIGN_LORDS[it] }
    @Test fun tenthHouseFromLeoLagnaIsTaurusLordVenus() {
        val f = LifeAreas.fact(10, 4, mapOf(Graha.VENUS to 11, Graha.SUN to 4), lords)
        assertEquals(1, f.signIndex); assertEquals(Graha.VENUS, f.lord); assertEquals(8, f.lordHouse); assertTrue(f.planets.isEmpty())
    }
    @Test fun firstHouseListsPlanetsInLagnaSign() {
        val f = LifeAreas.fact(1, 4, mapOf(Graha.SUN to 4, Graha.MARS to 4, Graha.MOON to 9), lords)
        assertEquals(4, f.signIndex); assertEquals(Graha.SUN, f.lord); assertEquals(1, f.lordHouse)
        assertEquals(setOf(Graha.SUN, Graha.MARS), f.planets.toSet())
    }
    @Test fun houseWrapsPastPisces() {
        assertEquals(0, LifeAreas.fact(4, 9, emptyMap(), lords).signIndex)
    }
    @Test fun fourAreasWithTraditionalHouses() {
        assertEquals(listOf("Career", "Finance", "Love", "Health"), LifeAreas.AREA_HOUSES.map { it.first })
        assertEquals(listOf(10), LifeAreas.AREA_HOUSES[0].third); assertEquals(listOf(5, 7), LifeAreas.AREA_HOUSES[2].third)
    }
    @Test fun describeSaysNoPlanetsPlainly() {
        assertTrue(LifeAreas.describe(LifeAreas.fact(10, 4, mapOf(Graha.VENUS to 11), lords)).contains("no planets in it"))
    }
}
