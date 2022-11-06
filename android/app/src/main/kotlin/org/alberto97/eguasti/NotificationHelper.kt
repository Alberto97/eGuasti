package org.alberto97.eguasti

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class NotificationHelper(private val context: Context) {
    companion object {
        private const val channelId = "outage_updates"
    }

    private val manager = ContextCompat.getSystemService(context, NotificationManager::class.java)!!

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val name = context.getString(R.string.outage_updates_notif_channel_name)
        val descriptionText = context.getString(R.string.outage_updates_notif_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(channelId, name, importance)
        mChannel.description = descriptionText
        manager.createNotificationChannel(mChannel)
    }

    fun postNotification(id: Int, title: String, text: String) {
        val notification = createNotification(title, text)
        manager.notify(id, notification)
    }

    private fun createNotification(title: String, text: String): Notification {
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channelId)
        }

        return builder.build()
    }
}