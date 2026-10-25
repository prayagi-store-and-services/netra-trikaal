package com.prayagi.netratrikaal.update

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Shown once when the app opens and a newer version exists. The only thing sent is a plain request to GitHub for the
 * public latest.json file (no birth details, no identifiers). The user chooses to update or to wait.
 */
@Composable
fun LaunchUpdatePrompt() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var release by remember { mutableStateOf<AppRelease?>(null) }
    var busy by remember { mutableStateOf(false) }
    var failure by remember { mutableStateOf<String?>(null) }
    var saved by remember { mutableStateOf<java.io.File?>(null) }
    var preloading by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val r = withContext(Dispatchers.IO) { AppUpdater.checkNow(context) }
        if (r is AppUpdater.CheckResult.Newer) {
            release = r.release

        }
    }
    val r = release ?: return
    AlertDialog(
        onDismissRequest = { if (!busy) release = null },
        title = { Text("नया अपडेट उपलब्ध है: v" + r.versionName) },
        text = {
            androidx.compose.foundation.layout.Column {
                Text(if (r.notes.isNotBlank()) r.notes else "सुधार और बग फ़िक्स।")
                if (saved != null) Text("अपडेट डाउनलोड हो चुका है। इंस्टॉल चुनने पर Android की पुष्टि खुलेगी। Play Protect चालू रखें।")
                if (busy) LinearProgressIndicator(modifier = androidx.compose.ui.Modifier.padding(top = 8.dp))
                failure?.let { Text(it) }
            }
        },
        confirmButton = {
            TextButton(enabled = !busy && !preloading, onClick = {
                scope.launch {
                    busy = true; failure = null
                    try {
                        val file = saved
                        if (file == null) {
                            saved = withContext(Dispatchers.IO) { AppUpdater.download(context, r) { _, _ -> } }
                        } else {
                            AppUpdater.install(context, file)
                        }
                    } catch (e: Exception) {
                        failure = plainFailure(e, "अपडेट नहीं हो सका। Settings में फिर कोशिश करें।")
                    }
                    busy = false
                }
            }) { Text(if (busy || preloading) "डाउनलोड हो रहा है..." else if (saved != null) "इंस्टॉल करें" else "डाउनलोड करें") }
        },
        dismissButton = { TextButton(enabled = !busy, onClick = { release = null }) { Text("बाद में") } }
    )
}
