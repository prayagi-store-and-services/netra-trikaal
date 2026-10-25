package com.prayagi.netratrikaal.update

import android.content.Context
import java.io.File

/** No APK downloader or installer remains. Eco owns app downloads and updates. */
object AppUpdater {
    const val ECO_PACKAGE = "com.prayagi.netraeco"
    internal val LEGACY_WORK_NAMES = listOf("netra_trikaal_update_save")
    fun retireLegacyUpdater(context: Context) {
        val c = context.applicationContext
        // Some test/disabled-startup contexts have no WorkManager, hence no persisted worker can run there.
        try {
            val wm = androidx.work.WorkManager.getInstance(c)
            LEGACY_WORK_NAMES.forEach { wm.cancelUniqueWork(it) }
        } catch (e: IllegalStateException) {
            android.util.Log.i("UpdaterRetirement", "WorkManager unavailable; legacy worker itself is inert", e)
        }
        val nm = c.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        nm.cancel(7432)
        if (android.os.Build.VERSION.SDK_INT >= 26) nm.deleteNotificationChannel("trikaal_update")
        listOf("trikaal_update_notice", "netra_app_update", "verified_update_artifacts").forEach {
            c.getSharedPreferences(it, Context.MODE_PRIVATE).edit().clear().apply()
        }
        Thread { File(c.cacheDir, "updates").deleteRecursively() }.start()
    }
    fun openEco(context: Context): Boolean {
        val launch = context.packageManager.getLaunchIntentForPackage(ECO_PACKAGE) ?: return false
        return try {
            context.startActivity(launch.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
            true
        } catch (_: android.content.ActivityNotFoundException) { false }
          catch (_: SecurityException) { false }
    }
}
