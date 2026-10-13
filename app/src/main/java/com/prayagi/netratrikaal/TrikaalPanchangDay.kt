package com.prayagi.netratrikaal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prayagi.trikaal.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val PdHm = DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)
private val PdDate = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH)

/** Everything the Panchang tab shows for one date, at that day's sunrise. Calculated on the phone. */
data class PanchangForDate(
    val sunrise: Instant, val sunset: Instant,
    val tithi: TithiSpan?, val nakshatra: String?, val yoga: Panchang.NamedSpan?, val karana: Panchang.NamedSpan?,
    val spans: List<Span>, val brahmaStart: Instant, val brahmaEnd: Instant
)

object PanchangForDateCalc {
    /** Brahma Muhurta: the 48 minutes that end 48 minutes before sunrise (96 to 48 minutes before). */
    fun brahma(sunrise: Instant): Pair<Instant, Instant> = sunrise.minusSeconds(96 * 60L) to sunrise.minusSeconds(48 * 60L)

    fun compute(date: LocalDate, place: PanchangPlace): Computation<PanchangForDate> {
        val noon = date.atTime(12, 0).atZone(place.zone).toInstant()
        val d = when (val r = Panchang.day(noon, place)) {
            is Computation.Available -> r.value
            is Computation.Unavailable -> return Computation.Unavailable(r.reason)
        }
        val at = d.sunrise
        val moon = (AstroCore.currentPositions(at) as? Computation.Available)?.value?.firstOrNull { it.graha == Graha.MOON }
        val (bs, be) = brahma(d.sunrise)
        return Computation.Available(PanchangForDate(
            d.sunrise, d.sunset,
            Panchang.day(at, place).let { (it as? Computation.Available)?.value?.tithis?.firstOrNull { t -> t.contains(at) } },
            moon?.let { NAKSHATRA_NAMES[it.nakshatra] },
            Panchang.yogaAt(at), Panchang.karanaAt(at), d.spans, bs, be))
    }
}

@Composable fun TrikaalPanchangDay(place: PanchangPlace) {
    var date by remember { mutableStateOf(LocalDate.now(place.zone)) }
    var data by remember { mutableStateOf<Computation<PanchangForDate>?>(null) }
    LaunchedEffect(date, place) { data = null; data = withContext(Dispatchers.Default) { PanchangForDateCalc.compute(date, place) } }
    val gold = Color(0xFFF2C14E)
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Palette.night2, contentColor = Color(0xFFFFF4DC))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Panchang / पंचांग · " + place.label, style = MaterialTheme.typography.titleMedium, color = gold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { date = date.minusDays(1) }) { Text("<", maxLines = 1) }
                Text(date.format(PdDate), maxLines = 1)
                TextButton(onClick = { date = date.plusDays(1) }) { Text(">", maxLines = 1) }
            }
            if (date != LocalDate.now(place.zone)) TextButton(onClick = { date = LocalDate.now(place.zone) }) { Text("Today / आज", maxLines = 1) }
            when (val c = data) {
                null -> Text("Calculating on this phone...")
                is Computation.Unavailable -> Text("Unavailable: ${c.reason}")
                is Computation.Available -> {
                    val v = c.value
                    val z: ZoneId = place.zone
                    fun hm(i: Instant) = PdHm.format(i.atZone(z))
                    Text("Values at sunrise, " + hm(v.sunrise) + ". End times are shown when the next one begins.", style = MaterialTheme.typography.bodySmall)
                    Text("Tithi / तिथि: " + (v.tithi?.let { it.name + " (" + it.paksha + "), till " + hm(it.end) } ?: "Unavailable"))
                    Text("Nakshatra / नक्षत्र: " + (v.nakshatra ?: "Unavailable"))
                    Text("Yoga / योग: " + (v.yoga?.let { it.name + ", till " + hm(it.end) } ?: "Unavailable"))
                    Text("Karana / करण: " + (v.karana?.let { it.name + ", till " + hm(it.end) } ?: "Unavailable"))
                    Text("Auspicious muhurat / शुभ मुहूर्त", style = MaterialTheme.typography.titleSmall, color = gold)
                    Text("Brahma Muhurta: " + hm(v.brahmaStart) + " to " + hm(v.brahmaEnd))
                    v.spans.filter { it.name.startsWith("Abhijit") }.forEach { Text(it.name + ": " + hm(it.start) + " to " + hm(it.end)) }
                    Text("Avoid / अशुभ काल", style = MaterialTheme.typography.titleSmall, color = gold)
                    listOf("Rahu Kaal", "Yamaganda", "Gulika Kaal").forEach { n ->
                        v.spans.firstOrNull { it.name.startsWith(n) }?.let { Text(n + ": " + hm(it.start) + " to " + hm(it.end)) }
                    }
                    Text("Sunrise " + hm(v.sunrise) + " · Sunset " + hm(v.sunset), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
