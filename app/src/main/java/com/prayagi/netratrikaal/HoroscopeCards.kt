package com.prayagi.netratrikaal

import com.prayagi.trikaal.Graha
import com.prayagi.trikaal.Rashifal

/** One card of the daily horoscope. [lines] are built only from the calculated chart and today's sky. */
internal data class HoroscopeCard(val key: String, val titleHi: String, val lines: List<String>)

private fun bhav(n: Int) = "${n}वें भाव"

/** Planet in the classical gochara list: favourable or not, from the user's own Moon sign. */
private fun fav(g: Graha, house: Int): Boolean = house in (Rashifal.FAVOURABLE_FROM_MOON[g] ?: emptySet())

private fun factLine(g: Graha, house: Int): String =
    "${grahaHi(g)} आपकी चंद्र राशि से ${bhav(house)} में है (" + (if (fav(g, house)) "अनुकूल" else "कमज़ोर") + ")।"

private fun tone(good: Int, total: Int, strong: String, mixed: String, careful: String): String =
    if (good == total) strong else if (good == 0) careful else mixed

/** Tara numbers that the Tarabala rule counts as auspicious (same list as [Rashifal]). */
internal fun taraGood(n: Int): Boolean = n in setOf(2, 4, 6, 8, 9)

/**
 * Builds the four cards (प्रेम, काम, ऊर्जा, सामाजिक). Each uses fixed classical rules (gochara from the birth Moon, Tarabala).
 * A card whose input is missing is left out completely. Nothing is random or generic: the same chart on the same day gives the same text.
 */
internal fun horoscopeCardsHi(houseFromMoon: Map<Graha, Int>, tara: Int?): List<HoroscopeCard> {
    val out = mutableListOf<HoroscopeCard>()
    val v = houseFromMoon[Graha.VENUS]; val mo = houseFromMoon[Graha.MOON]
    if (v != null && mo != null) {
        val good = listOf(fav(Graha.VENUS, v), fav(Graha.MOON, mo)).count { it }
        out += HoroscopeCard("love", "प्रेम", listOf(factLine(Graha.VENUS, v), factLine(Graha.MOON, mo),
            tone(good, 2, "रिश्तों में खुलकर और प्यार से बात करने के लिए अच्छा दिन है।",
                "रिश्तों में आज मिला-जुला असर है। बात शांति से रखें।",
                "रिश्तों में आज जल्दबाज़ी न करें। कड़वी बात टालें।")))
    }
    val su = houseFromMoon[Graha.SUN]; val sa = houseFromMoon[Graha.SATURN]
    if (su != null && sa != null) {
        val good = listOf(fav(Graha.SUN, su), fav(Graha.SATURN, sa)).count { it }
        out += HoroscopeCard("work", "काम", listOf(factLine(Graha.SUN, su), factLine(Graha.SATURN, sa),
            tone(good, 2, "काम और ज़िम्मेदारियों में आज अच्छी प्रगति के आसार हैं।",
                "काम में आज धीरे-धीरे आगे बढ़ें। एक बार में एक काम करें।",
                "काम में आज जोखिम और बड़े फ़ैसले टालें। ज़रूरी काम ही निपटाएँ।")))
    }
    val ma = houseFromMoon[Graha.MARS]
    if (ma != null && tara != null) {
        val good = listOf(fav(Graha.MARS, ma), taraGood(tara)).count { it }
        out += HoroscopeCard("energy", "ऊर्जा", listOf(factLine(Graha.MARS, ma),
            "आज की तारा संख्या $tara है (" + (if (taraGood(tara)) "शुभ" else "सावधानी") + ")।",
            tone(good, 2, "शरीर में ऊर्जा अच्छी रहेगी। हल्की कसरत या सैर फ़ायदेमंद है।",
                "ऊर्जा आज मध्यम रहेगी। खुद पर ज़्यादा बोझ न डालें।",
                "ऊर्जा आज कम रह सकती है। आराम और पानी का ध्यान रखें।")))
    }
    val me = houseFromMoon[Graha.MERCURY]; val ju = houseFromMoon[Graha.JUPITER]
    if (me != null && ju != null) {
        val good = listOf(fav(Graha.MERCURY, me), fav(Graha.JUPITER, ju)).count { it }
        out += HoroscopeCard("social", "सामाजिक", listOf(factLine(Graha.MERCURY, me), factLine(Graha.JUPITER, ju),
            tone(good, 2, "मिलने-जुलने और बातचीत के लिए अच्छा दिन है। पुराने साथियों से बात करें।",
                "लोगों से मिलना आज मिला-जुला रहेगा। बात सोच-समझकर करें।",
                "आज बहस से बचें और कम बोलें।")))
    }
    return out
}
