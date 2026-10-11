package com.prayagi.netratrikaal

import com.prayagi.trikaal.AstroCore
import com.prayagi.trikaal.BirthInput
import com.prayagi.trikaal.Computation
import com.prayagi.trikaal.Graha
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

/**
 * The user's own Moon sign (rashi) from the birth details saved on this phone.
 * The Rashifal screen shows only this one rashi. Nothing is guessed: without a full profile it says Unavailable.
 */
object OwnRashi {
    sealed class State {
        object Loading : State()
        data class Ready(val rashi: Int, val profileName: String) : State()
        data class Unavailable(val reason: String) : State()
    }

    /** The default profile, else the only saved profile, else none (never an arbitrary pick). */
    fun choose(profiles: List<TrikaalProfile>, defaultKey: String): TrikaalProfile? =
        profiles.firstOrNull { TrikaalProfiles.key(it) == defaultKey } ?: profiles.singleOrNull()

    /** Plain name of the first missing birth detail, or null when the profile is complete. */
    fun missing(p: TrikaalProfile): String? = when {
        p.date.isBlank() -> "date of birth"
        p.time.isBlank() -> "birth time"
        p.latitude.toDoubleOrNull() == null || p.longitude.toDoubleOrNull() == null -> "birth place"
        else -> null
    }

    const val NO_PROFILE = "Unavailable: add your birth details (date, time and place) in Kundli and save the profile to see your own rashifal."

    fun compute(profiles: List<TrikaalProfile>, defaultKey: String): State {
        val p = choose(profiles, defaultKey)
            ?: return State.Unavailable(if (profiles.isEmpty()) NO_PROFILE else "Unavailable: you have several saved profiles. Tap Make default on yours in Kundli, then open Rashifal again.")
        missing(p)?.let { return State.Unavailable("Unavailable: the $it is missing in this profile, so your rashi cannot be worked out. Nothing is guessed.") }
        return try {
            val b = BirthInput(p.name, LocalDate.parse(p.date), LocalTime.parse(p.time), p.zone.takeIf { it.isNotBlank() },
                p.latitude.toDoubleOrNull(), p.longitude.toDoubleOrNull(), p.place, p.offset.takeIf { it.isNotBlank() }?.let(ZoneOffset::of))
            when (val c = AstroCore.computeChart(b)) {
                is Computation.Available -> State.Ready(c.value.positions.single { it.graha == Graha.MOON }.rashi, p.name)
                is Computation.Unavailable -> State.Unavailable("Unavailable: " + c.reason)
            }
        } catch (e: Exception) {
            State.Unavailable("Unavailable: the saved birth details could not be read. Open Kundli and check them.")
        }
    }
}
