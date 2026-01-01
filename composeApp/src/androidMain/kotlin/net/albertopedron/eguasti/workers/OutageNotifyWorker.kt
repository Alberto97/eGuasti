package net.albertopedron.eguasti.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class OutageNotifyWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val task = OutageNotifyTask()
            val success = task.start()

            if (success) {
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}