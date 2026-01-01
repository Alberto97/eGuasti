package net.albertopedron.eguasti.tools

import android.content.Intent
import androidx.core.net.toUri
import net.albertopedron.eguasti.EGuastiApplication

actual class AppStoreUtils {

    companion object {
        private const val PACKAGE_NAME = "org.alberto97.eguasti"
        private const val PLAY_STORE_APP_URL = "https://play.google.com/store/apps/details?id=$PACKAGE_NAME"
    }

    private val app = EGuastiApplication.instance

    actual fun openForReview() {
        openUrl(PLAY_STORE_APP_URL)
    }

    actual fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = url.toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        app.startActivity(intent)
    }
}