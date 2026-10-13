package com.prayagi.netratrikaal

import com.prayagi.trikaal.Chart
import com.prayagi.trikaal.Graha
import com.prayagi.trikaal.Period
import com.prayagi.trikaal.Position
import java.time.Instant
import java.time.ZoneId

/** One block of a kundli topic. Every fact comes from the calculated chart; every meaning is a fixed traditional rule, never a per-person claim. */
internal data class TopicSection(val title: String, val lines: List<String>)

private val MONTHS_HI = listOf("जन॰", "फ़र॰", "मार्च", "अप्रैल", "मई", "जून", "जुलाई", "अग॰", "सित॰", "अक्टू॰", "नव॰", "दिस॰")
private val DASHA_ORDER = listOf(Graha.KETU, Graha.VENUS, Graha.SUN, Graha.MOON, Graha.MARS, Graha.RAHU, Graha.JUPITER, Graha.SATURN, Graha.MERCURY)
private val DASHA_YEARS_APP = listOf(7, 20, 6, 10, 7, 18, 16, 19, 17)

internal const val DASHA_CONVENTION = "दशा-वर्ष = 365.25 दिन (यहाँ की गणना-पद्धति)। दूसरी पद्धतियों से तारीख़ें 1-2 दिन अलग आ सकती हैं।"

/** Navamsha (D-9) longitude: each sign is cut into nine parts of 3 deg 20 min, counted on from Aries. */
internal fun navamshaLongitude(lon: Double): Double = (lon * 9.0) % 360.0

internal fun navamshaChart(c: Chart): Chart =
    c.copy(positions = c.positions.map { Position(it.graha, navamshaLongitude(it.longitude), it.speed) }, lagna = navamshaLongitude(c.lagna))

/** Splits a period into the nine sub-periods of the Vimshottari system, starting with the period's own lord. */
internal fun subPeriods(p: Period): List<Period> {
    val start = DASHA_ORDER.indexOf(p.lord)
    if (start < 0) return emptyList()
    val total = (p.end.toEpochMilli() - p.start.toEpochMilli()).toDouble()
    var s = p.start.toEpochMilli().toDouble()
    return (0..8).map { k ->
        val i = (start + k) % 9
        val e = if (k == 8) p.end.toEpochMilli().toDouble() else s + total * DASHA_YEARS_APP[i] / 120.0
        Period(DASHA_ORDER[i], Instant.ofEpochMilli(s.toLong()), Instant.ofEpochMilli(e.toLong())).also { s = e }
    }
}

internal fun fmtDate(i: Instant, zone: ZoneId): String {
    val d = i.atZone(zone).toLocalDate()
    return "${d.dayOfMonth} ${MONTHS_HI[d.monthValue - 1]} ${d.year}"
}

private fun bhavName(n: Int) = "${n}वाँ भाव"
private fun planetsIn(c: Chart, house: Int) = c.positions.filter { c.houseOf(it) == house }.map { it.graha }
private fun occupants(list: List<Graha>) = if (list.isEmpty()) "कोई ग्रह नहीं (खाली भाव का मतलब निष्क्रिय होना नहीं है)" else list.joinToString(", ") { grahaHi(it) }
private fun pos(c: Chart, g: Graha) = c.positions.single { it.graha == g }
private fun lordLine(c: Chart, house: Int): String {
    val lord = c.houseLords[house - 1]; val p = pos(c, lord)
    return "${bhavName(house)} के स्वामी ${grahaHi(lord)} हैं। वे ${SIGN_HI[p.rashi]} राशि में, ${bhavName(c.houseOf(p))} में हैं।"
}

private val HOUSE_THEME = listOf(
    Triple("स्वभाव और शुरुआत", "शरीर, स्वभाव और चीज़ों को शुरू करने का तरीक़ा।", "रोज़ की एक छोटी आदत (समय पर सोना, टहलना) पक्की करें।"),
    Triple("धन, परिवार और वाणी", "कमाई-बचत, परिवार और बोलने का ढंग।", "हर हफ़्ते खर्च और बचत का छोटा हिसाब लिखें।"),
    Triple("साहस और कौशल", "प्रयास, हुनर, संवाद और छोटे भाई-बहन।", "एक हुनर पर रोज़ 20 मिनट अभ्यास करें।"),
    Triple("घर और मन की शांति", "घर, माता, सुख-शांति और भीतरी स्थिरता।", "घर के किसी बुज़ुर्ग से हफ़्ते में एक बार खुलकर बात करें।"),
    Triple("शिक्षा और रचना", "पढ़ाई, बुद्धि, रचनात्मकता और संतान।", "जो सीख रहे हैं उसे किसी को समझाकर दोहराएँ।"),
    Triple("सेवा और प्रतिस्पर्धा", "सेवा, काम की दिनचर्या, प्रतिस्पर्धा और स्वास्थ्य की आदतें।", "काम की रोज़ की सूची बनाएँ और नींद का समय तय रखें।"),
    Triple("साझेदारी", "साझेदारी, सहयोग और क़रीबी रिश्तों का ढंग।", "किसी भी साझेदारी में बातें लिखित और साफ़ रखें।"),
    Triple("बदलाव और गहराई", "अचानक बदलाव, गहरी खोज, साझा संसाधन और विरासत।", "आपात-ख़र्च के लिए छोटा अलग फ़ंड रखें।"),
    Triple("भाग्य और मार्गदर्शन", "शिक्षक, मार्गदर्शन, मान्यताएँ और लंबी यात्राएँ।", "किसी अनुभवी व्यक्ति से महीने में एक बार सलाह लें।"),
    Triple("कर्म और पद", "काम, पद, सार्वजनिक छवि और ज़िम्मेदारी।", "अपने काम का एक मापने लायक लक्ष्य लिखें।"),
    Triple("आय और मित्र-मंडल", "आय, लाभ, मित्र-मंडल और बड़े लक्ष्य।", "आय के हर स्रोत को एक कॉपी में रोज़ दर्ज करें।"),
    Triple("ख़र्च और एकांत", "ख़र्च, एकांत, विश्राम और विदेश से जुड़े विषय।", "हर महीने के ख़र्च की श्रेणियाँ देखें और एक घटाने लायक चुनें।")
)

/** Topic: all twelve houses. Saral mode: meaning, tip and limit. Vedic mode adds the chart facts (planets, house lord). */
internal fun houseSections(c: Chart, vedic: Boolean): List<TopicSection> = (1..12).map { h ->
    val (name, meaning, tip) = HOUSE_THEME[h - 1]
    val sign = (c.lagnaRashi + h - 1) % 12
    val lines = mutableListOf<String>()
    lines += "राशि: ${SIGN_HI[sign]}। ग्रह: ${occupants(planetsIn(c, h))}।"
    if (vedic) lines += lordLine(c, h)
    lines += "पारंपरिक अर्थ: $meaning"
    lines += "व्यावहारिक सलाह: $tip"
    lines += "यह क्या साबित नहीं करता: यह भाव अकेले किसी घटना का वादा या फ़ैसला नहीं करता।"
    TopicSection("${bhavName(h)}: $name", lines)
}

private fun marsHouseFrom(c: Chart, ref: Position): Int = (pos(c, Graha.MARS).rashi - ref.rashi + 12) % 12 + 1

/** Topic: marriage and relationships. Facts only; no timing, no description of a partner, no verdict. */
internal fun vivahSections(c: Chart): List<TopicSection> {
    val d9 = navamshaChart(c)
    val venus = pos(c, Graha.VENUS)
    val out = mutableListOf<TopicSection>()
    out += TopicSection("सातवाँ भाव (साझेदारी)", listOf(
        "सातवें भाव की राशि: ${SIGN_HI[(c.lagnaRashi + 6) % 12]}।",
        "इसमें स्थित ग्रह: ${occupants(planetsIn(c, 7))}।",
        lordLine(c, 7),
        "शुक्र ${SIGN_HI[venus.rashi]} राशि में, ${bhavName(c.houseOf(venus))} में है।"))
    out += TopicSection("नवांश (D-9) में सातवाँ भाव", listOf(
        "नवांश लग्न: ${SIGN_HI[d9.lagnaRashi]}। नवांश का सातवाँ भाव ${SIGN_HI[(d9.lagnaRashi + 6) % 12]} है।",
        "नवांश के सातवें भाव में ग्रह: ${occupants(planetsIn(d9, 7))}।",
        "पारंपरिक रूप से नवांश को रिश्तों की गहराई देखने में सहायक माना जाता है।"))
    val moon = pos(c, Graha.MOON)
    out += TopicSection("मंगल की स्थिति (दोष घोषित नहीं)", listOf(
        "मंगल लग्न से ${marsHouseFrom(c, Position(Graha.SUN, c.lagna, 0.0))}वें भाव में है।",
        "मंगल चंद्र से ${marsHouseFrom(c, moon)}वें भाव में है।",
        "मंगल शुक्र से ${marsHouseFrom(c, venus)}वें भाव में है।",
        "मंगल दोष की गिनती परंपरा पर निर्भर करती है (कहीं 1, 4, 7, 8, 12 भाव, कहीं 2 भी)। इसलिए यहाँ दोष घोषित नहीं किया गया।"))
    out += TopicSection("सीमा और सलाह", listOf(
        "यह भाग विवाह होने, न होने, उसके समय या जीवनसाथी के बारे में कुछ नहीं कहता।",
        "व्यावहारिक सलाह: किसी भी रिश्ते में अपेक्षाएँ साफ़ शब्दों में कहें और सुनने का समय दें।"))
    return out
}

/** Topic: career and business. Facts only; no income, success or timing claims. */
internal fun careerSections(c: Chart): List<TopicSection> {
    val d9 = navamshaChart(c)
    return listOf(
        TopicSection("दसवाँ भाव (कर्म)", listOf(
            "दसवें भाव की राशि: ${SIGN_HI[(c.lagnaRashi + 9) % 12]}। ग्रह: ${occupants(planetsIn(c, 10))}।", lordLine(c, 10))),
        TopicSection("दूसरा और ग्यारहवाँ भाव (धन और आय)", listOf(
            "दूसरा भाव: ${SIGN_HI[(c.lagnaRashi + 1) % 12]}, ग्रह: ${occupants(planetsIn(c, 2))}।", lordLine(c, 2),
            "ग्यारहवाँ भाव: ${SIGN_HI[(c.lagnaRashi + 10) % 12]}, ग्रह: ${occupants(planetsIn(c, 11))}।", lordLine(c, 11))),
        TopicSection("तीसरा और छठा भाव (कौशल और सेवा)", listOf(
            "तीसरा भाव: ${SIGN_HI[(c.lagnaRashi + 2) % 12]}, ग्रह: ${occupants(planetsIn(c, 3))}।",
            "छठा भाव: ${SIGN_HI[(c.lagnaRashi + 5) % 12]}, ग्रह: ${occupants(planetsIn(c, 6))}।")),
        TopicSection("नवांश (D-9) लग्न", listOf("नवांश लग्न ${SIGN_HI[d9.lagnaRashi]} है।", "दशमांश (D-10) इस ऐप में अभी उपलब्ध नहीं है, इसलिए नहीं दिखाया गया।")),
        TopicSection("सीमा और सलाह", listOf(
            "यह भाग आय, सफलता या समय के बारे में कोई अनुमान नहीं देता। यह केवल पारंपरिक भाव-स्थिति दिखाता है।",
            "व्यावहारिक सलाह: आय और ख़र्च रोज़ दर्ज करें, ग्राहकों या सहकर्मियों से महीने में एक बार राय लें और एक सीखने लायक कौशल चुनें।")))
}

private val GRAHA_THEME = mapOf(
    Graha.SUN to "पहचान, अधिकार और आत्मविश्वास", Graha.MOON to "मन, भावनाएँ और घर-परिवार",
    Graha.MARS to "ऊर्जा, साहस और मेहनत", Graha.MERCURY to "बुद्धि, संवाद और व्यापार",
    Graha.JUPITER to "ज्ञान, विस्तार और मार्गदर्शन", Graha.VENUS to "संबंध, सुख और कला",
    Graha.SATURN to "अनुशासन, ज़िम्मेदारी और धैर्य", Graha.RAHU to "असामान्य अनुभव, बदलाव और उलझन", Graha.KETU to "विरक्ति, खोज और अंतर्मुखता")

/** Topic: running dasha. All four levels are computed here; nothing about events or dates of events is predicted. */
internal fun dashaSections(c: Chart, periods: List<Period>, now: Instant): List<TopicSection> {
    val zone = ZoneId.of(c.input.tzId)
    val maha = periods.firstOrNull { it.contains(now) } ?: return listOf(TopicSection("दशा", listOf("आज की तारीख़ इस 120-वर्षीय चक्र से बाहर है, इसलिए दशा नहीं दिखाई गई।")))
    val antar = maha.children.firstOrNull { it.contains(now) }
    val praty = antar?.let { subPeriods(it).firstOrNull { p -> p.contains(now) } }
    val sook = praty?.let { subPeriods(it) }
    val curSook = sook?.firstOrNull { it.contains(now) }
    val nextSook = sook?.let { l -> curSook?.let { l.getOrNull(l.indexOf(it) + 1) } }
    fun row(label: String, p: Period?) = p?.let { "$label: ${grahaHi(it.lord)} (${fmtDate(it.start, zone)} से ${fmtDate(it.end, zone)})" }
    val levels = listOfNotNull(row("महादशा", maha), row("अंतर्दशा", antar), row("प्रत्यंतर", praty), row("सूक्ष्म", curSook))
    val out = mutableListOf<TopicSection>()
    out += TopicSection("अभी चल रही दशा", levels + listOf("स्तर एक-दूसरे के भीतर चलते हैं: महादशा में अंतर्दशा, उसमें प्रत्यंतर और उसमें सूक्ष्म।", DASHA_CONVENTION))
    row("अगला सूक्ष्म", nextSook)?.let { out += TopicSection("आगे का अगला चरण", listOf(it)) }
    out += TopicSection("पारंपरिक अर्थ", listOfNotNull(
        "महादशा ${grahaHi(maha.lord)}: पारंपरिक रूप से ${GRAHA_THEME[maha.lord]} से जुड़े विषय उभरते माने जाते हैं।",
        antar?.let { "अंतर्दशा ${grahaHi(it.lord)}: ${GRAHA_THEME[it.lord]} के विषय साथ में सक्रिय माने जाते हैं।" },
        "यह गारंटी नहीं है। इसमें किसी घटना या तारीख़ की भविष्यवाणी नहीं है।",
        "व्यावहारिक सलाह: इस दौर के विषयों पर छोटे, नापने लायक लक्ष्य रखें और महीने के अंत में समीक्षा करें."))
    return out
}

internal enum class Verdict(val hi: String) { MET("पूरी होती है"), NOT_MET("पूरी नहीं होती"), UNSURE("पुष्टि नहीं हो सकती") }
internal data class YogResult(val name: String, val verdict: Verdict, val rule: String)

private val OWN = mapOf(Graha.MARS to setOf(0, 7), Graha.MERCURY to setOf(2, 5), Graha.JUPITER to setOf(8, 11), Graha.VENUS to setOf(1, 6), Graha.SATURN to setOf(9, 10))
private val EXALT = mapOf(Graha.MARS to 9, Graha.MERCURY to 5, Graha.JUPITER to 3, Graha.VENUS to 11, Graha.SATURN to 6)
private val MAHAPURUSHA = mapOf(Graha.MARS to "रुचक", Graha.MERCURY to "भद्र", Graha.JUPITER to "हंस", Graha.VENUS to "मालव्य", Graha.SATURN to "शश")

/** Each rule is stated in plain words and checked only against the calculated chart. No remedies are offered. */
internal fun yogResults(c: Chart): List<YogResult> {
    val out = mutableListOf<YogResult>()
    val moon = pos(c, Graha.MOON); val jup = pos(c, Graha.JUPITER)
    fun v(b: Boolean) = if (b) Verdict.MET else Verdict.NOT_MET
    out += YogResult("गजकेसरी", v((jup.rashi - moon.rashi + 12) % 12 in setOf(0, 3, 6, 9)), "गुरु चंद्र से केंद्र (1, 4, 7, 10) में हो")
    out += YogResult("बुधादित्य", v(pos(c, Graha.SUN).rashi == pos(c, Graha.MERCURY).rashi), "सूर्य और बुध एक ही राशि में हों (अस्त होने से इसकी शक्ति घट सकती है, वह यहाँ नहीं जाँची)")
    out += YogResult("उच्च का गुरु", v(jup.rashi == EXALT[Graha.JUPITER]), "गुरु कर्क राशि में हो")
    for ((g, name) in MAHAPURUSHA) {
        val p = pos(c, g)
        val strong = p.rashi in (OWN[g] ?: emptySet()) || p.rashi == EXALT[g]
        val kendra = c.houseOf(p) in setOf(1, 4, 7, 10)
        out += YogResult("$name योग (पंच महापुरुष)", v(strong && kendra), "${grahaHi(g)} अपनी या उच्च राशि में हो और लग्न से केंद्र में हो")
    }
    val near = c.positions.filter { it.graha in setOf(Graha.MARS, Graha.MERCURY, Graha.JUPITER, Graha.VENUS, Graha.SATURN) }
        .any { val d = (it.rashi - moon.rashi + 12) % 12; d == 0 || d == 1 || d == 11 }
    out += YogResult("केमद्रुम (मूल शर्त)", v(!near), "चंद्र के साथ या उससे दूसरे/बारहवें भाव में मंगल-शनि जैसा कोई ग्रह (सूर्य, राहु, केतु छोड़कर) न हो। भंग होने की शर्तें यहाँ नहीं जाँची गईं।")
    val rahu = pos(c, Graha.RAHU).longitude
    val seven = c.positions.filter { it.graha != Graha.RAHU && it.graha != Graha.KETU }
    val side = seven.map { ((it.longitude - rahu + 360) % 360) < 180 }
    out += YogResult("कालसर्प (कड़ी परिभाषा)", v(side.all { it } || side.none { it }), "सूर्य से शनि तक सातों ग्रह राहु-केतु की एक ही तरफ़ हों। इसके कई भेद विवादित हैं।")
    val l2 = pos(c, c.houseLords[1]); val l11 = pos(c, c.houseLords[10])
    val rel = (l11.rashi - l2.rashi + 12) % 12
    out += YogResult("धन योग (सीमित नियम)", v(rel == 0 || rel == 6), "दूसरे और ग्यारहवें भाव के स्वामी एक ही राशि में हों या एक-दूसरे से सातवें हों")
    out += YogResult("पितृ दोष", Verdict.UNSURE, "इसकी कोई एक सर्वमान्य गणना-विधि नहीं है, इसलिए यहाँ पुष्टि नहीं की गई")
    return out
}

internal fun yogSections(c: Chart): List<TopicSection> {
    val r = yogResults(c)
    return r.map { TopicSection(it.name + ": " + it.verdict.hi, listOf("नियम: " + it.rule + "।")) } +
        TopicSection("ज़रूरी बात", listOf(
            "मंगल दोष की स्थिति विवाह वाले भाग में भाव-तथ्य के रूप में दी गई है; दोष घोषित नहीं किया गया।",
            "यहाँ कोई उपाय या डर नहीं दिया जाता। ये नियम पारंपरिक हैं और अलग-अलग ग्रंथों में अलग हैं।"))
}
