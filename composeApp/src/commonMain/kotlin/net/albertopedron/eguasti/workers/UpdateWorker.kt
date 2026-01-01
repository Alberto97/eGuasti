//package net.albertopedron.eguasti.workers
//
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import androidx.work.CoroutineWorker
//import androidx.work.ForegroundInfo
//import androidx.work.WorkerParameters
//import eguasti.composeapp.generated.resources.Res
//import eguasti.composeapp.generated.resources.ic_launcher_foreground
//import eguasti.composeapp.generated.resources.update_notification_channel
//import eguasti.composeapp.generated.resources.update_notification_title
//import net.albertopedron.eguasti.domain.UpdateDatabaseUseCase
//
//class UpdateWorker(
//    private val appContext: Context,
//    workerParams: WorkerParameters,
//) : CoroutineWorker(appContext, workerParams) {
//    companion object {
//        const val LOG_TAG = "UpdateWorker"
//    }
//
//    private val notificationManager = NotificationManagerCompat.from(appContext)
//    private val channelId = "eguasti_updates"
//    private val updateDatabaseUseCase = UpdateDatabaseUseCase()
//
//    override suspend fun doWork(): Result {
//        try {
//            updateDatabaseUseCase.execute()
//        } catch (e: Exception) {
//            Log.e(LOG_TAG, e.stackTraceToString())
//            return Result.failure()
//        }
//
//        return Result.success()
//    }
//
//    override suspend fun getForegroundInfo(): ForegroundInfo {
//        return createForegroundInfo()
//    }
//
//    /**
//     * Create ForegroundInfo required to run a Worker in a foreground service.
//     */
//    private fun createForegroundInfo(): ForegroundInfo {
//        // Use a different id for each Notification.
//        val notificationId = 1
//        return ForegroundInfo(notificationId, createNotification())
//    }
//
//    /**
//     * Create the notification and required channel (O+) for running work
//     * in a foreground service.
//     */
//    private fun createNotification(): Notification {
//
//        val title = appContext.getString(Res.string.update_notification_title)
//        val builder = NotificationCompat.Builder(appContext, channelId)
//            .setContentTitle(title)
//            .setSmallIcon(Res.drawable.ic_launcher_foreground)
//            .setOngoing(true)
//            .setProgress(100, 0, true)
//
//        val channelName = appContext.getString(Res.string.update_notification_channel)
//        createNotificationChannel(channelId, channelName).also {
//            builder.setChannelId(it.id)
//        }
//
//        return builder.build()
//    }
//
//    /**
//     * Create the required notification channel for O+ devices.
//     */
//    @Suppress("SameParameterValue")
//    private fun createNotificationChannel(
//        id: String,
//        name: String
//    ): NotificationChannel {
//        return NotificationChannel(
//            id, name, NotificationManager.IMPORTANCE_LOW
//        ).also { channel ->
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//}