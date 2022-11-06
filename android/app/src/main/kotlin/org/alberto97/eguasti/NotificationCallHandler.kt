package org.alberto97.eguasti

import android.content.Context
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class NotificationCallHandler(context: Context) : MethodChannel.MethodCallHandler {
    companion object {
        const val CREATE_NOTIFICATION_CHANNEL = "createNotificationChannel"
    }

    private val notificationHelper = NotificationHelper(context)

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            CREATE_NOTIFICATION_CHANNEL -> {
                notificationHelper.createNotificationChannel()
                result.success(true)
            }
            else -> result.notImplemented()
        }
    }
}