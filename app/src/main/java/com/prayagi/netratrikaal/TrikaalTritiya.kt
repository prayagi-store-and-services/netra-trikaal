package com.prayagi.netratrikaal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.prayagi.trikaal.*
import java.time.DayOfWeek
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val TriHm = DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)
private val TriDate = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH)

/** Traditional weekday practices. General information from tradition, not a promise of any result. */
object WeekdayRemedies {
    data class Entry(val lord: String, val deity: String, val mantra: String, val colour: String)
    fun of(d: DayOfWeek): Entry = when (d) {
        DayOfWeek.SUNDAY -> Entry("Sun (Surya)", "Surya", "Om Suryaya Namah", "Red or orange")
        DayOfWeek.MONDAY -> Entry("Moon (Chandra)", "Shiva", "Om Somaya Namah", "White")
        DayOfWeek.TUESDAY -> Entry("Mars (Mangala)", "Hanuman", "Om Angarakaya Namah", "Red")
        DayOfWeek.WEDNESDAY -> Entry("Mercury (Budha)", "Vishnu or Ganesha", "Om Budhaya Namah", "Green")
        DayOfWeek.THURSDAY -> Entry("Jupiter (Guru)", "Vishnu or Brihaspati", "Om Brihaspataye Namah", "Yellow")
        DayOfWeek.FRIDAY -> Entry("Venus (Shukra)", "Lakshmi", "Om Shukraya Namah", "White or light colours")
        DayOfWeek.SATURDAY -> Entry("Saturn (Shani)", "Shani or Hanuman", "Om Shanaischaraya Namah", "Dark blue or black")
    }
}

private data class TriData(val day: VedicDay, val parts: List<Choghadiya.Part>)

@Composable fun TrikaalTritiya(place: PanchangPlace) {
    val gold = Color(0xFFF2C14E)
    var data by remember { mutableStateOf<Computation<TriData>?>(null) }
    var ingress by remember { mutableStateOf<List<TrikaalIngress.Change?>?>(null) }
    LaunchedEffect(place) {
        data = null; ingress = null
        val now = Instant.now()
        data = withContext(Dispatchers.Default) {
            when (val r = Panchang.day(now, place)) {
                is Computation.Available -> Computation.Available(TriData(r.value, Choghadiya.parts(r.value.weekday, r.value.sunrise, r.value.sunset, r.value.nextSunrise)))
                is Computation.Unavailable -> Computation.Unavailable(r.reason)
            }
        }
        ingress = withContext(Dispatchers.Default) {
            listOf(Graha.SUN, Graha.MOON, Graha.MARS, Graha.MERCURY, Graha.JUPITER, Graha.VENUS, Graha.SATURN).map { TrikaalIngress.next(it, now) }
        }
    }
    val cardColors = CardDefaults.cardColors(containerColor = Palette.night2, contentColor = Color(0xFFFFF4DC))
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Card(Modifier.fillMaxWidth(), colors = cardColors) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Special Muhurat / विशेष मुहूर्त · " + place.label, style = MaterialTheme.typography.titleMedium, color = gold)
                when (val c = data) {
                    null -> Text("Calculating on this phone...")
                    is Computation.Unavailable -> Text("Unavailable: ${c.reason}")
                    is Computation.Available -> {
                        val v = c.value.day; val z = place.zone
                        fun hm(i: Instant) = TriHm.format(i.atZone(z))
                        val (bs, be) = PanchangForDateCalc.brahma(v.sunrise)
                        Text("Brahma Muhurta: " + hm(bs) + " to " + hm(be))
                        v.spans.filter { it.name.startsWith("Abhijit") }.forEach { Text(it.name + ": " + hm(it.start) + " to " + hm(it.end)) }
                        Text("Choghadiya / चौघड़िया (sunrise to next sunrise)", style = MaterialTheme.typography.titleSmall, color = gold)
                        val now = Instant.now()
                        c.value.parts.forEach { p ->
                            val q = when (p.quality) { Choghadiya.Quality.GOOD -> "good"; Choghadiya.Quality.NEUTRAL -> "neutral"; Choghadiya.Quality.AVOID -> "avoid" }
                            val mark = if (now >= p.start && now < p.end) "> " else ""
                            Text(mark + p.name + " (" + q + "): " + hm(p.start) + " to " + hm(p.end), maxLines = 1)
                        }
                        Text("Traditional timing method. It is general guidance, not a prediction.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        Card(Modifier.fillMaxWidth(), colors = cardColors) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Transit / गोचर · next sign change", style = MaterialTheme.typography.titleMedium, color = gold)
                val list = ingress
                if (list == null) Text("Calculating on this phone...")
                else {
                    list.forEach { ch ->
                        if (ch != null) Text(ch.graha.label + ": " + SIGN_NAMES[ch.from] + " to " + SIGN_NAMES[ch.to] + " on " + TriDate.format(ch.at.atZone(place.zone)), maxLines = 2)
                    }
                    if (list.any { it == null }) Text("Some planets do not change sign within the next 400 days, so they are not listed.", style = MaterialTheme.typography.bodySmall)
                    Text("Sidereal (Lahiri). A retrograde planet can step back into the previous sign.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Card(Modifier.fillMaxWidth(), colors = cardColors) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Remedies / उपाय · today", style = MaterialTheme.typography.titleMedium, color = gold)
                val wd = (data as? Computation.Available)?.value?.day?.weekday
                if (wd == null) Text("Unavailable: weekday not calculated.")
                else {
                    val r = WeekdayRemedies.of(wd)
                    Text("Day lord: " + r.lord)
                    Text("Traditionally remembered with: " + r.deity)
                    Text("Mantra: " + r.mantra)
                    Text("Traditional colour: " + r.colour)
                    Text("These are traditional practices shared as general information. They are not medical, legal or financial advice and carry no guaranteed result.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
