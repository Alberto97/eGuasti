package net.albertopedron.eguasti.tools

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import net.albertopedron.eguasti.EGuastiApplication
import net.albertopedron.eguasti.workers.OutageNotifyWorker
import java.util.concurrent.TimeUnit

actual class WorkScheduler actual constructor() {

    private val app: Application = EGuastiApplication.instance

    companion object {
        const val OUTAGE_NOTIFY_TASK_UNIQUE_NAME = "outageNotify"
    }

    actual fun schedule() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<OutageNotifyWorker>(
            15, // repeat interval in minutes
            TimeUnit.MINUTES
        )
            .setInitialDelay(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(app).enqueueUniquePeriodicWork(
            OUTAGE_NOTIFY_TASK_UNIQUE_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    actual fun unschedule() {
        WorkManager.getInstance(app)
            .cancelUniqueWork(OUTAGE_NOTIFY_TASK_UNIQUE_NAME)
    }
}