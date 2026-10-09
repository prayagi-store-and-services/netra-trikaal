package com.prayagi.netratrikaal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prayagi.trikaal.PanchangPlace

private val TAB_TITLES = listOf("Kundli", "Horoscope", "Match Making", "Panchang", "Other", "Settings")

/**
 * Tabbed layout, shown only after [RedesignGate] switches on (11 Oct 2026 00:00 device-local).
 * It reuses the existing screens; nothing is calculated differently.
 */
@Composable
fun TrikaalTabs(place: PanchangPlace, onPlace: (PanchangPlace) -> Unit, onKundli: (Boolean) -> Unit) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    ScrollableTabRow(selectedTabIndex = tab, edgePadding = 0.dp) {
        TAB_TITLES.forEachIndexed { i, t -> Tab(selected = tab == i, onClick = { tab = i }, text = { Text(t) }) }
    }
    androidx.compose.runtime.LaunchedEffect(tab) { if (tab != 0) onKundli(false) }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        when (tab) {
            0 -> TrikaalContent(onKundli)
            1 -> { TrikaalToday(place) }
            2 -> { Text("Match Making: Unavailable. This is not built yet and no result is shown.", style = MaterialTheme.typography.bodyMedium) }
            3 -> { TrikaalTicker(place); TrikaalToday(place) }
            4 -> { TrikaalSoon() }
            else -> { TrikaalLocationChoice(place, onPlace) }
        }
    }
}
