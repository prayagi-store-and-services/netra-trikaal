package com.prayagi.netratrikaal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prayagi.trikaal.PanchangPlace

/**
 * Tabbed layout, shown only after [RedesignGate] switches on (11 Oct 2026 00:00 device-local).
 * It reuses the existing screens; nothing is calculated differently.
 */
@Composable
fun TrikaalTabs(place: PanchangPlace, onPlace: (PanchangPlace) -> Unit, onKundli: (Boolean) -> Unit, tab: Int, onTab: (Int) -> Unit = {}) {
    androidx.compose.runtime.LaunchedEffect(tab) { if (tab != 0) onKundli(false) }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        when (tab) {
            0 -> { TrikaalInsights(place); TrikaalExplore(onTab); TrikaalContent(onKundli) }
            1 -> { TrikaalHoroscope() }
            2 -> { TrikaalTicker(place); TrikaalToday(place, 1) }
            else -> { TrikaalLocationChoice(place, onPlace); com.prayagi.netratrikaal.update.UpdateCheckCard() }
        }
    }
}
