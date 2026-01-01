package net.albertopedron.eguasti.tools

import kotlinx.coroutines.flow.MutableStateFlow

object DeviceConfigurationChanges {
    val darkTheme = MutableStateFlow(false)
}