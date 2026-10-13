package com.prayagi.netratrikaal

/** The commonly taught Vastu Shastra direction guardians and elements. A traditional reference only. Sources differ on some elements, so this is the common textbook assignment. */
object Vastu {
    data class Direction(val name: String, val nameHi: String, val deity: String, val element: String)
    val TABLE = listOf(
        Direction("North", "उत्तर", "Kubera", "Water"),
        Direction("North-East", "ईशान", "Ishana (Shiva)", "Water"),
        Direction("East", "पूर्व", "Indra", "Air"),
        Direction("South-East", "आग्नेय", "Agni", "Fire"),
        Direction("South", "दक्षिण", "Yama", "Earth"),
        Direction("South-West", "नैऋत्य", "Nirriti", "Earth"),
        Direction("West", "पश्चिम", "Varuna", "Water"),
        Direction("North-West", "वायव्य", "Vayu", "Air"),
        Direction("Centre", "ब्रह्मस्थान", "Brahma", "Space")
    )
}
