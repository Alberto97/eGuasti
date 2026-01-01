package net.albertopedron.eguasti.tools

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual class AppStoreUtils {

    companion object {
        private const val APP_STORE_URL = "itms-apps://itunes.apple.com/app/<APP_ID>"
    }

    actual fun openForReview() {
        openUrl(APP_STORE_URL)
    }

    actual fun openUrl(url: String) {
        val url = NSURL(string = url)
        UIApplication.sharedApplication.openURL(url, mapOf<Any?, Any>(), null)
    }
}