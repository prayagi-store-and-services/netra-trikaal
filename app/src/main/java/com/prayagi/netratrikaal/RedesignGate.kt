package com.prayagi.netratrikaal

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime

/** New look switches on at 11 Oct 2026 00:00 device-local time. Device clock only, no network. */
object RedesignGate {
    val SWITCH_AT: LocalDateTime = LocalDateTime.of(2026, 10, 11, 0, 0)
    fun isOn(now: LocalDateTime): Boolean = !now.isBefore(SWITCH_AT)
    fun isOn(): Boolean = isOn(LocalDateTime.now())
}

/** One palette for the whole app. Old: deep indigo. New: purple. */
object Palette {
    private val on get() = RedesignGate.isOn()
    val night: Color get() = if (on) Color(0xFF160B33) else Color(0xFF0A0E2A)
    val night2: Color get() = if (on) Color(0xFF2B1A58) else Color(0xFF1B2150)
    val nightHigh: Color get() = if (on) Color(0xFF3B2578) else Color(0xFF262D66)
    val skyTop: Color get() = if (on) Color(0xFF0E0724) else Color(0xFF070A22)
    val skyMid: Color get() = if (on) Color(0xFF241256) else Color(0xFF151245)
    val skyLow: Color get() = if (on) Color(0xFF3F1F73) else Color(0xFF2A1A52)
    val outlineVariant: Color get() = if (on) Color(0xFF7A5FB8) else Color(0xFF5A5F99)
}
