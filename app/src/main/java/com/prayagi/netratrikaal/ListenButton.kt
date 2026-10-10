package com.prayagi.netratrikaal

import android.speech.tts.TextToSpeech
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/** Text read aloud for a kundli summary. Pure, so it can be unit tested. */
internal fun listenSummary(lagna: String, moon: String, current: String?): String =
    "Lagna $lagna. Moon sign $moon. " + (current?.let { "Current period $it." } ?: "Current period is Unavailable.")

/**
 * Listen button: reads the given text with the phone's own text-to-speech. Nothing is sent anywhere.
 * If the phone has no speech voice the button says so instead of failing silently.
 */
@Composable
internal fun ListenButton(text: String) {
    val ctx = LocalContext.current.applicationContext
    var ready by remember { mutableStateOf<Boolean?>(null) }
    var speaking by remember { mutableStateOf(false) }
    val tts = remember {
        var engine: TextToSpeech? = null
        engine = TextToSpeech(ctx) { status ->
            val e = engine
            if (status == TextToSpeech.SUCCESS && e != null) {
                val r = e.setLanguage(Locale.US)
                ready = r != TextToSpeech.LANG_MISSING_DATA && r != TextToSpeech.LANG_NOT_SUPPORTED
            } else ready = false
        }
        engine
    }
    DisposableEffect(Unit) { onDispose { try { tts.stop(); tts.shutdown() } catch (_: Throwable) { } } }
    when (ready) {
        false -> Text("Listen: Unavailable, this phone has no speech voice installed.")
        else -> OutlinedButton(enabled = ready == true, onClick = {
            if (speaking) { tts.stop(); speaking = false } else {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "trikaal_listen"); speaking = true
            }
        }) { Text(if (speaking) "Stop" else "Listen") }
    }
}
