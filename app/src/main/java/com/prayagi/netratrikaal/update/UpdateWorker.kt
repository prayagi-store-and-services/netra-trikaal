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

/** About every 6 hours, online only: only check release metadata and notify. APK download needs an explicit choice. */
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
                UpdateNotice.notifyOnce(applicationContext, r.release)
            }
        } catch (_: Exception) {
            // Try again at the next run.
        }
        Result.success()
    }
}
