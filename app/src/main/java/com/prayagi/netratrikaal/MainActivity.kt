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
        com.prayagi.netratrikaal.update.UpdateWorkScheduler.start(applicationContext)
        // Ask once (Android 13+) so the "update downloaded" notice can show. Declining changes nothing else.
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            val sp = getSharedPreferences("trikaal_update_notice", MODE_PRIVATE)
            if (!sp.getBoolean("asked", false) &&
                checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                sp.edit().putBoolean("asked", true).apply()
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 7432)
            }
        }
        setContent {
            MaterialTheme { Surface(Modifier.fillMaxSize()) { TrikaalCard(initiallyOpen = true); com.prayagi.netratrikaal.update.LaunchUpdatePrompt() } }
        }
    }
}
