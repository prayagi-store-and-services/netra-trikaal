package com.prayagi.netratrikaal.update

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat

/** One notification per saved version: "downloaded, tap to install". Tapping opens the app, which offers the saved file. */
object UpdateNotice {
    private const val CHANNEL_ID = "trikaal_update"
    private const val PREFS = "trikaal_update_notice"

    fun notifyOnce(c: Context, release: AppRelease) {
        val prefs = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (release.versionCode <= prefs.getLong("notified_code", 0L)) return
        if (Build.VERSION.SDK_INT >= 33 &&
            c.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return
        val nm = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(NotificationChannel(CHANNEL_ID, "App updates", NotificationManager.IMPORTANCE_DEFAULT))
        }
        val launch = c.packageManager.getLaunchIntentForPackage(c.packageName) ?: return
        val open = PendingIntent.getActivity(c, 0, launch, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val n = NotificationCompat.Builder(c, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("Update downloaded")
            .setContentText("Version " + release.versionName + " is downloaded. Tap to install.")
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        nm.notify(7432, n)
        prefs.edit().putLong("notified_code", release.versionCode).apply()
    }
}
