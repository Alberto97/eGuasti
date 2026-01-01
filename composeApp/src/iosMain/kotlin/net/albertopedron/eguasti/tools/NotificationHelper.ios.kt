package net.albertopedron.eguasti.tools

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UserNotifications.UNAuthorizationStatus
import platform.UserNotifications.*
import platform.UserNotifications.UNUserNotificationCenter

actual class NotificationHelper {
    actual suspend fun initialize() {
        // No-op
    }

    actual fun postNotification(id: Int, title: String, text: String) {
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(text)
            setSound(UNNotificationSound.defaultSound)
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, false)

        val identifier = id.toString()
        val request = UNNotificationRequest.requestWithIdentifier(identifier, content, trigger)

        UNUserNotificationCenter.currentNotificationCenter()
            .addNotificationRequest(request) { error ->
                error?.let {
                    println("Failed to schedule notification: ${it.localizedDescription}")
                }
            }
    }

    actual suspend fun requestPermission(): Boolean {
        return suspendCancellableCoroutine { cont ->
            UNUserNotificationCenter.currentNotificationCenter()
                .requestAuthorizationWithOptions(
                    options = UNAuthorizationOptionAlert or
                            UNAuthorizationOptionSound or
                            UNAuthorizationOptionBadge
                ) { granted, _ ->
                    cont.resume(granted) { _, _, _ -> }
                }
        }
    }

    actual companion object {
        actual suspend fun hasPermission(): Boolean {
            val status = getNotificationAuthorizationStatus()
            return when (status) {
                UNAuthorizationStatusAuthorized,
                UNAuthorizationStatusProvisional,
                UNAuthorizationStatusEphemeral -> true
                UNAuthorizationStatusDenied -> false
                UNAuthorizationStatusNotDetermined -> false
                else -> false
            }
        }

        suspend fun getNotificationAuthorizationStatus(): UNAuthorizationStatus? =
            suspendCancellableCoroutine { cont ->
                UNUserNotificationCenter.currentNotificationCenter()
                    .getNotificationSettingsWithCompletionHandler { settings ->
                        cont.resume(settings?.authorizationStatus)  { _, _, _ -> }
                    }
            }
    }
}