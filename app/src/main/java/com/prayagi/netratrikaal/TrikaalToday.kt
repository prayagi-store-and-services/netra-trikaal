package com.prayagi.netratrikaal

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
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val TodayHm = DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)

/**
 * First-screen card for people with no saved profile, so the first screen is never just an empty form.
 * Everything is calculated on the phone for the chosen place (Prayagraj if no location was shared). Nothing is sent anywhere.
 */
@Composable fun TrikaalToday(place: PanchangPlace) {
    var day by remember { mutableStateOf<Computation<VedicDay>?>(null) }
    var moon by remember { mutableStateOf<Computation<List<Position>>?>(null) }
    LaunchedEffect(place) {
        day = null
        val now = Instant.now()
        day = withContext(Dispatchers.Default) { Panchang.day(now, place) }
        moon = withContext(Dispatchers.Default) { AstroCore.currentPositions(now) }
    }
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2150), contentColor = Color(0xFFFFF4DC))) {
        Column(Modifier.padding(16.dp)) {
            Text("Today / आज · ${place.label}", style = MaterialTheme.typography.titleMedium, color = Color(0xFFF2C14E))
            when (val d = day) {
                null -> Text("Calculating on this phone...")
                is Computation.Unavailable -> Text("Unavailable: ${d.reason}")
                is Computation.Available -> {
                    val v = d.value
                    val now = Instant.now()
                    val t = v.tithis.firstOrNull { it.contains(now) }
                    Text("Tithi / तिथि: " + (t?.let { "${it.name} (${it.paksha}), ends ${TodayHm.format(it.end.atZone(place.zone))}" } ?: "Unavailable"))
                    val m = (moon as? Computation.Available)?.value?.firstOrNull { it.graha == Graha.MOON }
                    Text("Nakshatra / नक्षत्र: " + (m?.let { NAKSHATRA_NAMES[it.nakshatra] } ?: "Unavailable"))
                    listOf("Rahu Kaal", "Yamaganda", "Gulika Kaal", "Abhijit").forEach { n ->
                        val s = v.spans.firstOrNull { it.name.startsWith(n) }
                        Text("$n: " + (s?.let { "${TodayHm.format(it.start.atZone(place.zone))} to ${TodayHm.format(it.end.atZone(place.zone))}" } ?: "Unavailable"))
                    }
                    Text("Sunrise ${TodayHm.format(v.sunrise.atZone(place.zone))} · Sunset ${TodayHm.format(v.sunset.atZone(place.zone))}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Text("Enter your birth details below to see your own kundli.", style = MaterialTheme.typography.bodySmall)
        }
    }
}
