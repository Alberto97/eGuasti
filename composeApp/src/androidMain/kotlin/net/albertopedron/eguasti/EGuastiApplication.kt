package net.albertopedron.eguasti

import android.app.Application

class EGuastiApplication : Application() {
    companion object {
        lateinit var instance: EGuastiApplication
    }

    init {
        instance = this
    }

    var notificationPermissionGranted: (value: Boolean) -> Unit = {}

    var askNotificationPermission: () -> Unit = {}
}
