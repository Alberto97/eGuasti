package net.albertopedron.eguasti

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {

    private val requestNotificationsPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            EGuastiApplication.instance.notificationPermissionGranted(isGranted)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setupNotificationPermissionHandler()

        setContent {
            EGuastiApp()
        }
    }

    private fun setupNotificationPermissionHandler() {
        EGuastiApplication.instance.askNotificationPermission = {
            requestNotificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
