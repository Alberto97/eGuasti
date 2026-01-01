package net.albertopedron.eguasti.util

import net.albertopedron.eguasti.EGuastiApplication


actual object AppVersionProvider {

    private val app = EGuastiApplication.instance
    private val pInfo = app.packageManager.getPackageInfo(app.packageName, 0)

    actual val versionName: String
        get() = pInfo.versionName.toString()
    actual val versionCode: String
        get() = pInfo.longVersionCode.toString()
}
