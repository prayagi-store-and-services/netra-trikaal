package com.prayagi.netratrikaal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

/** Single screen: the Trikaal card opens full screen on launch. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.prayagi.netratrikaal.update.AppUpdater.retireLegacyUpdater(this)
        if (com.prayagi.netratrikaal.geo.GeoGuard.isBlocked(this)) {
            val pad = (24 * resources.displayMetrics.density).toInt()
            val box = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                setPadding(pad, pad, pad, pad)
            }
            box.addView(android.widget.TextView(this).apply {
                text = "This app is not available in your region."
                textSize = 20f
                gravity = android.view.Gravity.CENTER
            })
            box.addView(android.widget.Button(this).apply {
                text = "Close"
                setOnClickListener { finishAffinity() }
            })
            setContentView(box)
            return
        }
        com.prayagi.netratrikaal.festival.JayMataDi.greetIfDue(this)
        setContent {
            MaterialTheme { Surface(Modifier.fillMaxSize()) { TrikaalCard(initiallyOpen = true) } }
        }
    }
}
