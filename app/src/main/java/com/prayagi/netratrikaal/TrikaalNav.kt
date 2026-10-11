package com.prayagi.netratrikaal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prayagi.trikaal.*
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Bottom navigation tabs (new design). Labels only describe screens that exist; unbuilt ones say so inside the screen. */
val TRIKAAL_TABS = listOf("Kundli" to "\u2726", "Horoscope" to "\u2609", "Match" to "\u2665", "Panchang" to "\u25F7", "Other" to "\u25A6", "Settings" to "\u2699")

private val InsightHm = DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)

@Composable fun TrikaalBottomBar(selected: Int, onSelect: (Int) -> Unit) {
    NavigationBar(containerColor = Palette.night2, contentColor = Color(0xFFFFF4DC)) {
        TRIKAAL_TABS.forEachIndexed { i, (label, icon) ->
            NavigationBarItem(
                selected = selected == i,
                onClick = { onSelect(i) },
                icon = { Text(icon, color = if (selected == i) Color(0xFFF2C14E) else Color(0xFFFFF4DC)) },
                label = { Text(label, maxLines = 1, style = MaterialTheme.typography.labelSmall, color = if (selected == i) Color(0xFFF2C14E) else Color(0xFFFFF4DC)) }
            )
        }
    }
}

@Composable private fun InsightTile(title: String, value: String, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Palette.nightHigh, contentColor = Color(0xFFFFF4DC))) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = Color(0xFFF2C14E), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Text(value, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}

/** Today's insights: real values calculated on the phone for the chosen place. Anything not calculated says Unavailable. */
@Composable fun TrikaalInsights(place: PanchangPlace) {
    var day by remember { mutableStateOf<Computation<VedicDay>?>(null) }
    var sky by remember { mutableStateOf<Computation<List<Position>>?>(null) }
    LaunchedEffect(place) {
        day = null
        val now = Instant.now()
        day = withContext(Dispatchers.Default) { Panchang.day(now, place) }
        sky = withContext(Dispatchers.Default) { AstroCore.currentPositions(now) }
    }
    val now = Instant.now()
    val v = (day as? Computation.Available)?.value
    val tithi = v?.tithis?.firstOrNull { it.contains(now) }?.let { "${it.name} (${it.paksha})" } ?: "Unavailable"
    val moon = (sky as? Computation.Available)?.value?.firstOrNull { it.graha == Graha.MOON }
    val nak = moon?.let { NAKSHATRA_NAMES[it.nakshatra] } ?: "Unavailable"
    fun span(n: String): String = v?.spans?.firstOrNull { it.name.startsWith(n) }?.let { "${InsightHm.format(it.start.atZone(place.zone))}-${InsightHm.format(it.end.atZone(place.zone))}" } ?: "Unavailable"
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Palette.night2, contentColor = Color(0xFFFFF4DC))) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Today's Insights / आज · ${place.label}", style = MaterialTheme.typography.titleMedium, color = Color(0xFFF2C14E))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                InsightTile("Tithi", tithi, Modifier.weight(1f))
                InsightTile("Nakshatra", nak, Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                InsightTile("Rahu Kaal", span("Rahu Kaal"), Modifier.weight(1f))
                InsightTile("Abhijit", span("Abhijit"), Modifier.weight(1f))
            }
            if (day == null) Text("Calculating on this phone...", style = MaterialTheme.typography.bodySmall)
        }
    }
}
