package com.prayagi.netratrikaal

import com.prayagi.trikaal.*

/**
 * Career, Finance, Love and Health as the traditional house significations of the birth chart (whole-sign houses).
 * Career: 10th house. Finance: 2nd and 11th. Love: 5th and 7th. Health: 1st and 6th.
 * It lists the facts of each house (sign, its lord, where that lord sits, planets in it). It gives no rating, score or prediction.
 */
object LifeAreas {
    data class HouseFact(val house: Int, val signIndex: Int, val lord: Graha, val lordHouse: Int?, val planets: List<Graha>)
    class Area(val title: String, val note: String, val houses: List<HouseFact>)

    val AREA_HOUSES = listOf(
        Triple("Career", "10th house of work and standing", listOf(10)),
        Triple("Finance", "2nd house of savings and 11th house of gains", listOf(2, 11)),
        Triple("Love", "5th house of romance and 7th house of partnership", listOf(5, 7)),
        Triple("Health", "1st house of the body and 6th house of illness and routine", listOf(1, 6))
    )

    /** Pure part: lagna sign, each graha's sign index, and the sign lords. */
    fun fact(house: Int, lagnaRashi: Int, rashiOf: Map<Graha, Int>, lords: (Int) -> Graha): HouseFact {
        val sign = (lagnaRashi + house - 1) % 12
        val lord = lords(sign)
        val lordHouse = rashiOf[lord]?.let { (it - lagnaRashi + 12) % 12 + 1 }
        val here = rashiOf.filter { it.value == sign }.keys.toList()
        return HouseFact(house, sign, lord, lordHouse, here)
    }

    fun build(c: Chart): List<Area> {
        val m = c.positions.associate { it.graha to it.rashi }
        return AREA_HOUSES.map { (t, n, hs) -> Area(t, n, hs.map { fact(it, c.lagnaRashi, m) { s -> SIGN_LORDS[s] } }) }
    }

    fun describe(f: HouseFact): String =
        "House ${f.house}: ${SIGN_NAMES[f.signIndex]}, lord ${f.lord.label}" +
            (f.lordHouse?.let { " placed in house $it" } ?: "") +
            (if (f.planets.isEmpty()) ", no planets in it" else ", with " + f.planets.joinToString(", ") { it.label })
}
