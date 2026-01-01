package net.albertopedron.eguasti.tools

import net.albertopedron.eguasti.EGuastiApplication

actual object Files {
    actual val root: String = EGuastiApplication.instance.filesDir.absolutePath
}