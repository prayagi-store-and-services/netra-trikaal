package com.prayagi.netratrikaal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.sp
import com.prayagi.trikaal.*
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Bottom navigation tabs (new design). Only screens that have real content get a tab. Unbuilt ones (match making, planned features) are hidden until they work. */
val TRIKAAL_TABS = listOf("Kundli" to "\u2726", "Horoscope" to "\u2609", "Panchang" to "\u25F7", "Tritiya" to "\u2728", "Other" to "\u2630", "Settings" to "\u2699")

private val InsightHm = DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)

@Composable fun TrikaalBottomBar(selected: Int, onSelect: (Int) -> Unit) {
    androidx.compose.foundation.layout.Row(
        Modifier.fillMaxWidth().background(Palette.night2).padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TRIKAAL_TABS.forEachIndexed { i, (label, icon) ->
            val c = if (selected == i) Color(0xFFF2C14E) else Color(0xFFFFF4DC)
            Column(
                Modifier.weight(1f).clickable { onSelect(i) },
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Text(icon, color = c)
                Text(label, color = c, fontSize = 9.sp, maxLines = 1, softWrap = false, textAlign = TextAlign.Center, fontWeight = if (selected == i) androidx.compose.ui.text.font.FontWeight.Bold else null)
            }
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
    var yoga by remember { mutableStateOf<Panchang.NamedSpan?>(null) }
    var karana by remember { mutableStateOf<Panchang.NamedSpan?>(null) }
    LaunchedEffect(place) {
        day = null
        val now = Instant.now()
        day = withContext(Dispatchers.Default) { Panchang.day(now, place) }
        sky = withContext(Dispatchers.Default) { AstroCore.currentPositions(now) }
        yoga = withContext(Dispatchers.Default) { Panchang.yogaAt(now) }
        karana = withContext(Dispatchers.Default) { Panchang.karanaAt(now) }
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
            fun until(n: Panchang.NamedSpan?): String = n?.let { it.name + " (till " + InsightHm.format(it.end.atZone(place.zone)) + ")" } ?: "Unavailable"
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                InsightTile("Yoga", until(yoga), Modifier.weight(1f))
                InsightTile("Karana", until(karana), Modifier.weight(1f))
            }
            if (day == null) Text("Calculating on this phone...", style = MaterialTheme.typography.bodySmall)
        }
    }
}

/** Home "Explore" tiles. Only sections that work today get a tile; the rest appear when they are built. */
@Composable fun TrikaalExplore(onTab: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Explore Netra Trikaal", style = MaterialTheme.typography.titleMedium, color = Color(0xFFF2C14E))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(1, 2).forEach { i ->
                val (label, icon) = TRIKAAL_TABS[i]
                Card(Modifier.weight(1f).clickable { onTab(i) }, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Palette.nightHigh, contentColor = Color(0xFFFFF4DC))) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                        Text(icon, color = Color(0xFFF2C14E)); Text(label, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(3, 4).forEach { i ->
                val (label, icon) = TRIKAAL_TABS[i]
                Card(Modifier.weight(1f).clickable { onTab(i) }, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Palette.nightHigh, contentColor = Color(0xFFFFF4DC))) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                        Text(icon, color = Color(0xFFF2C14E)); Text(label, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}
