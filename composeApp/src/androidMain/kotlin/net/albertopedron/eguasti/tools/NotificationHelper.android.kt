package net.albertopedron.eguasti.tools

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import eguasti.composeapp.generated.resources.Res
import eguasti.composeapp.generated.resources.outage_updates_notif_channel_description
import eguasti.composeapp.generated.resources.outage_updates_notif_channel_name
import kotlinx.coroutines.suspendCancellableCoroutine
import net.albertopedron.eguasti.EGuastiApplication
import net.albertopedron.eguasti.shared.R
import org.jetbrains.compose.resources.getString
import kotlin.coroutines.resume

actual class NotificationHelper {

    private val app = EGuastiApplication.instance
    private val manager = ContextCompat.getSystemService(app, NotificationManager::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    actual suspend fun initialize() {
        createNotificationChannel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun createNotificationChannel() {
        val name = getString( Res.string.outage_updates_notif_channel_name)
        val descriptionText = getString(Res.string.outage_updates_notif_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        manager?.createNotificationChannel(mChannel)
    }

    actual fun postNotification(id: Int, title: String, text: String) {
        val notification = createNotification(title, text)
        manager?.notify(id, notification)
    }

    private fun createNotification(title: String, text: String): Notification {
        val builder = NotificationCompat.Builder(app, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setChannelId(CHANNEL_ID)

        return builder.build()
    }

    actual suspend fun requestPermission(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            app.notificationPermissionGranted = { result -> continuation.resume(result) }
            app.askNotificationPermission()
        }
    }

    actual companion object {
        const val CHANNEL_ID = "outage_updates"
        actual suspend fun hasPermission(): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when (ContextCompat.checkSelfPermission(
                    EGuastiApplication.instance,
                    Manifest.permission.POST_NOTIFICATIONS
                )) {
                    PackageManager.PERMISSION_GRANTED -> true
                    else -> false
                }
            } else {
                true
            }
        }
    }
}