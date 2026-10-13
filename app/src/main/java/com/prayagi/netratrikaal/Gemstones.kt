package com.prayagi.netratrikaal

/** The traditional (Vedic) planet and gemstone correspondences. A reference list only: nothing is worked out for a person and nothing is recommended. */
object Gemstones {
    data class Entry(val planet: String, val planetHi: String, val gem: String, val gemHi: String)
    val TABLE = listOf(
        Entry("Sun", "सूर्य", "Ruby", "माणिक"),
        Entry("Moon", "चंद्र", "Pearl", "मोती"),
        Entry("Mars", "मंगल", "Red Coral", "मूंगा"),
        Entry("Mercury", "बुध", "Emerald", "पन्ना"),
        Entry("Jupiter", "गुरु", "Yellow Sapphire", "पुखराज"),
        Entry("Venus", "शुक्र", "Diamond", "हीरा"),
        Entry("Saturn", "शनि", "Blue Sapphire", "नीलम"),
        Entry("Rahu", "राहु", "Hessonite (Gomed)", "गोमेद"),
        Entry("Ketu", "केतु", "Cat's Eye", "लहसुनिया")
    )
    fun gemFor(planet: String): Entry? = TABLE.firstOrNull { it.planet.equals(planet, ignoreCase = true) }
}
