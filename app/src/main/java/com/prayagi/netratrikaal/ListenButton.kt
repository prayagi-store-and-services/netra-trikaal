package com.prayagi.netratrikaal

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Locale

/** Text read aloud for a kundli summary. Pure, so it can be unit tested. */
internal fun listenSummary(lagna: String, moon: String, current: String?): String =
    "Lagna $lagna. Moon sign $moon. " + (current?.let { "Current period $it." } ?: "Current period is Unavailable.")

/**
 * Holds transient audio focus while the app speaks (ecosystem rule, 19 Oct 2026): other media
 * pauses (not mutes) and resumes afterwards. A watchdog releases the focus even when no
 * utterance callback ever arrives. minSdk is 26, so only the AudioFocusRequest API is needed.
 */
private class SpeechFocus(private val ctx: Context) {
    private var req: AudioFocusRequest? = null
    private val handler = Handler(Looper.getMainLooper())
    private val watchdog = Runnable { release() }

    fun acquire() {
        if (req != null) return
        val am = ctx.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        try {
            val r = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANT)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                .build()
            if (am.requestAudioFocus(r) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                req = r
                handler.postDelayed(watchdog, 60_000L)
            }
        } catch (_: Exception) { }
    }

    fun release() {
        val r = req ?: return
        req = null
        handler.removeCallbacks(watchdog)
        val am = ctx.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        try { am.abandonAudioFocusRequest(r) } catch (_: Exception) { }
    }
}

/**
 * Listen button: reads the given text with the phone's own text-to-speech. Nothing is sent anywhere.
 * If the phone has no speech voice the button says so instead of failing silently.
 * The Test button speaks one clearly labelled test line through the same path - only a test,
 * never a real reading (standing rule, 19 Oct 2026).
 */
@Composable
internal fun ListenButton(text: String) {
    val ctx = LocalContext.current.applicationContext
    var ready by remember { mutableStateOf<Boolean?>(null) }
    var speaking by remember { mutableStateOf(false) }
    val focus = remember { SpeechFocus(ctx) }
    val tts = remember {
        var engine: TextToSpeech? = null
        engine = TextToSpeech(ctx) { status ->
            val e = engine
            if (status == TextToSpeech.SUCCESS && e != null) {
                val r = e.setLanguage(Locale.US)
                ready = r != TextToSpeech.LANG_MISSING_DATA && r != TextToSpeech.LANG_NOT_SUPPORTED
                e.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(id: String?) { }
                    override fun onDone(id: String?) { speaking = false; focus.release() }
                    @Deprecated("Deprecated in Java")
                    override fun onError(id: String?) { speaking = false; focus.release() }
                })
            } else ready = false
        }
        engine
    }
    DisposableEffect(Unit) { onDispose { focus.release(); try { tts.stop(); tts.shutdown() } catch (_: Throwable) { } } }
    when (ready) {
        false -> Text("Listen: Unavailable, this phone has no speech voice installed.")
        else -> Row {
            OutlinedButton(enabled = ready == true, onClick = {
                if (speaking) { tts.stop(); speaking = false; focus.release() } else {
                    focus.acquire()
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "trikaal_listen"); speaking = true
                }
            }) { Text(if (speaking) "Stop" else "Listen") }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(enabled = ready == true && !speaking, onClick = {
                focus.acquire()
                tts.speak(
                    "Test announcement. The Trikaal listen voice is working.",
                    TextToSpeech.QUEUE_FLUSH, null, "trikaal_listen_test"
                ); speaking = true
            }) { Text("Test") }
        }
    }
}
