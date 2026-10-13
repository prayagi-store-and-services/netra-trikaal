package com.prayagi.netratrikaal.update

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** About every 6 hours, online only: when a newer version exists, save it (rules in [PreDownload]) so installing is one tap. */
object UpdateWorkScheduler {
    private const val WORK_NAME = "netra_trikaal_update_save"

    fun start(context: Context) {
        try {
            val request = PeriodicWorkRequestBuilder<UpdateWorker>(6, TimeUnit.HOURS)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
        } catch (_: Exception) { /* background save unavailable; saving on app open still works */ }
    }
}

class UpdateWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val r = AppUpdater.checkNow(applicationContext)
            if (r is AppUpdater.CheckResult.Newer) {
                val saved = AppUpdater.savedFile(applicationContext, r.release) != null
                var ok = saved
                if (!saved && PreDownload.allowedNow(applicationContext, false, r.release.size)) {
                    ok = try { AppUpdater.download(applicationContext, r.release); true } catch (_: Exception) { false }
                }
                if (ok) UpdateNotice.notifyOnce(applicationContext, r.release)
            }
        } catch (_: Exception) {
            // Try again at the next run.
        }
        Result.success()
    }
}
