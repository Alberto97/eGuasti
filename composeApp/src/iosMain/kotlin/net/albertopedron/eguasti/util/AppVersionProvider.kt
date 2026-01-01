package net.albertopedron.eguasti.util

import platform.Foundation.NSBundle

actual object AppVersionProvider {
    actual val versionName: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as String
    actual val versionCode: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as String
}
