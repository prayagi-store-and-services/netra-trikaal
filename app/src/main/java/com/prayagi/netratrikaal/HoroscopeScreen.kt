package com.prayagi.netratrikaal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prayagi.trikaal.*
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private class HoroscopeData(val moonRashi: Int, val name: String, val cards: List<HoroscopeCard>, val dasha: String?, val date: String)

/**
 * Horoscope tab: today's reading for the saved profile, in four cards (प्रेम, काम, ऊर्जा, सामाजिक).
 * Everything is calculated on this phone from the birth chart and today's sky. A card with no calculable data is not shown.
 */
@Composable fun TrikaalHoroscope() {
    val context = LocalContext.current
    var state by remember { mutableStateOf<Any?>(null) } // null = calculating, String = message, HoroscopeData = ready
    LaunchedEffect(Unit) {
        state = withContext(Dispatchers.Default) {
            try {
                val profiles = TrikaalProfiles.read(context)
                val p = OwnRashi.choose(profiles, TrikaalProfiles.defaultKey(context))
                    ?: return@withContext (if (profiles.isEmpty()) "अपना राशिफल देखने के लिए कुंडली टैब में जन्म तारीख, समय और जगह भरकर प्रोफ़ाइल सेव करें।"
                        else "कई प्रोफ़ाइल सेव हैं। कुंडली टैब में अपनी प्रोफ़ाइल पर 'Make default' दबाएँ।")
                OwnRashi.missing(p)?.let { return@withContext "इस प्रोफ़ाइल में जानकारी अधूरी है, इसलिए राशिफल नहीं बन सकता। कुंडली टैब में इसे पूरा करें।" }
                val b = BirthInput(p.name, LocalDate.parse(p.date), LocalTime.parse(p.time), p.zone.takeIf { it.isNotBlank() },
                    p.latitude.toDoubleOrNull(), p.longitude.toDoubleOrNull(), p.place, p.offset.takeIf { it.isNotBlank() }?.let(ZoneOffset::of))
                val chart = (AstroCore.computeChart(b) as? Computation.Available)?.value
                    ?: return@withContext "गणना अभी उपलब्ध नहीं है।"
                val now = Instant.now()
                val tr = (AstroCore.gochar(now, chart) as? Computation.Available)?.value
                    ?: return@withContext "आज के ग्रहों की गणना अभी उपलब्ध नहीं है।"
                val natalMoon = chart.positions.single { it.graha == Graha.MOON }
                val todayMoon = tr.firstOrNull { it.position.graha == Graha.MOON }
                val tara = todayMoon?.let { Rashifal.taraNumber(natalMoon.nakshatra, it.position.nakshatra) }
                val cards = horoscopeCardsHi(tr.associate { it.position.graha to it.houseFromMoon }, tara)
                val cur = currentPeriod(vimshottariDasha(chart), now)
                val dasha = cur?.let { "चल रही दशा: ${grahaHi(it.first.lord)} महादशा" + (it.second?.let { a -> ", ${grahaHi(a.lord)} अंतर्दशा" } ?: "") }
                HoroscopeData(natalMoon.rashi, p.name, cards, dasha, LocalDate.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("d MMM yyyy")))
            } catch (e: Exception) {
                "सेव की हुई जानकारी पढ़ी नहीं जा सकी। कुंडली टैब में उसे जाँचें।"
            }
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (val s = state) {
            null -> Text("आपके फ़ोन पर गणना हो रही है...", style = MaterialTheme.typography.bodyMedium)
            is String -> Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Palette.night2)) {
                Text(s, Modifier.padding(16.dp), color = Color(0xFFFFF4DC))
            }
            is HoroscopeData -> {
                Text("आज का राशिफल", Modifier.background(Color(0xFF7C3AED), RoundedCornerShape(50)).padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Text(SIGN_HI[s.moonRashi], fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFF4DC), maxLines = 1)
                Text("चंद्र राशि" + (if (s.name.isNotBlank()) " · ${s.name}" else "") + " · ${s.date}", fontSize = 13.sp, color = Color(0xFFD8CFEA))
                s.cards.forEach { c ->
                    val accent = when (c.key) { "love" -> Color(0xFFE879A6); "work" -> Color(0xFFF2C14E); "energy" -> Color(0xFF5BB8F0); else -> Color(0xFF4FD1B5) }
                    val icon = when (c.key) { "love" -> "♥"; "work" -> "▣"; "energy" -> "⚡"; else -> "☺" }
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Palette.night2, contentColor = Color(0xFFFFF4DC)),
                        border = BorderStroke(1.dp, accent.copy(alpha = 0.5f))) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("$icon  ${c.titleHi}", color = accent, fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = 2.sp, maxLines = 1)
                            c.lines.forEach { Text(it, fontSize = 15.sp, lineHeight = 22.sp) }
                        }
                    }
                }
                if (s.cards.isEmpty()) Text("आज के लिए कोई कार्ड नहीं बन सका।", fontSize = 13.sp)
                s.dasha?.let { Text(it, fontSize = 13.sp, color = Color(0xFFD8CFEA)) }
                Text("यह गणना वैदिक नियमों (चंद्र राशि से गोचर, तारा बल) पर आपके फ़ोन में हुई है। कुछ भी बाहर नहीं भेजा गया। यह निश्चितता नहीं है।", fontSize = 12.sp, color = Color(0xFFD8CFEA))
            }
        }
    }
}
