package com.prayagi.netratrikaal.update
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
/** Persisted legacy work exits without networking or installer effects. */
class UpdateWorker(c:Context,p:WorkerParameters):CoroutineWorker(c,p){override suspend fun doWork():Result=Result.success()}
object UpdateWorkScheduler{ fun start(c:Context){AppUpdater.retireLegacyUpdater(c)} }
