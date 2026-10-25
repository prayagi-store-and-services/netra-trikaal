package com.prayagi.netratrikaal.update
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
/** Retained solely so persisted legacy work safely succeeds without network or APK operations. */
class UpdateWorker(context:Context,params:WorkerParameters):CoroutineWorker(context,params) {
 override suspend fun doWork():Result = Result.success()
}
