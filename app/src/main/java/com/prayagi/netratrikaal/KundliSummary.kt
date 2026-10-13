package com.prayagi.netratrikaal

import com.prayagi.trikaal.Graha

internal val SIGN_HI = listOf("मेष", "वृषभ", "मिथुन", "कर्क", "सिंह", "कन्या", "तुला", "वृश्चिक", "धनु", "मकर", "कुंभ", "मीन")
internal val NAKSHATRA_HI = listOf("अश्विनी", "भरणी", "कृत्तिका", "रोहिणी", "मृगशिरा", "आर्द्रा", "पुनर्वसु", "पुष्य", "आश्लेषा", "मघा", "पूर्वा फाल्गुनी", "उत्तरा फाल्गुनी", "हस्त", "चित्रा", "स्वाति", "विशाखा", "अनुराधा", "ज्येष्ठा", "मूल", "पूर्वाषाढ़ा", "उत्तराषाढ़ा", "श्रवण", "धनिष्ठा", "शतभिषा", "पूर्वा भाद्रपद", "उत्तरा भाद्रपद", "रेवती")
internal fun grahaHi(g: Graha): String = when (g) {
    Graha.SUN -> "सूर्य"; Graha.MOON -> "चंद्र"; Graha.MARS -> "मंगल"; Graha.MERCURY -> "बुध"
    Graha.JUPITER -> "गुरु"; Graha.VENUS -> "शुक्र"; Graha.SATURN -> "शनि"; Graha.RAHU -> "राहु"; Graha.KETU -> "केतु"
}

/**
 * Plain-Hindi kundli summary. Every line is a fact taken from the calculated chart; nothing is predicted or invented.
 * A line whose data is missing (for example the dasha) is left out instead of showing a placeholder.
 */
internal fun kundliSummaryHi(
    lagnaRashi: Int, lagnaLord: Graha, moonRashi: Int, moonNakshatra: Int, moonPada: Int, moonHouse: Int,
    maha: Graha?, antar: Graha?, mahaEnd: String?, retrograde: List<Graha>
): List<String> {
    val out = mutableListOf<String>()
    out += "आपका लग्न ${SIGN_HI[lagnaRashi]} है। इसके स्वामी ${grahaHi(lagnaLord)} हैं।"
    out += "आपकी चंद्र राशि ${SIGN_HI[moonRashi]} है। जन्म नक्षत्र ${NAKSHATRA_HI[moonNakshatra]} (चरण $moonPada) है।"
    out += "जन्म के समय चंद्रमा आपकी कुंडली के ${moonHouse}वें भाव में था।"
    if (maha != null) {
        val sub = if (antar != null) " और ${grahaHi(antar)} की अंतर्दशा" else ""
        val end = if (mahaEnd != null) " यह महादशा $mahaEnd तक है।" else ""
        out += "अभी ${grahaHi(maha)} की महादशा$sub चल रही है।$end"
    }
    out += if (retrograde.isEmpty()) "जन्म के समय कोई मुख्य ग्रह वक्री नहीं था।"
    else "जन्म के समय वक्री ग्रह: " + retrograde.joinToString(", ") { grahaHi(it) } + "।"
    return out
}
