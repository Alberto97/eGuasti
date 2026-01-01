package net.albertopedron.eguasti.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import net.albertopedron.eguasti.data.MapProvider
import net.albertopedron.eguasti.data.MapProviderRepository
import net.albertopedron.eguasti.data.MapProviders
import net.albertopedron.eguasti.data.Persistence
import net.albertopedron.eguasti.tools.AppStoreUtils
import net.albertopedron.eguasti.util.AppVersionProvider
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class SettingsViewModel(
    private val persistence: Persistence = Persistence,
    private val appStoreUtils: AppStoreUtils = AppStoreUtils(),
    private val mapProviderRepository: MapProviderRepository = MapProviderRepository()
): ViewModel() {

    val appVersion = "${AppVersionProvider.versionName} (${AppVersionProvider.versionCode})"
    val dbVersion = persistence.getLastDbUpdate().map { millis -> formatLastDbUpdateDate(millis) }

    val availableMapProviders: List<MapProvider> = mapProviderRepository.getAvailable()

    val currentMapProvider: StateFlow<MapProvider?> = mapProviderRepository.getCurrent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _showMapProviderDialog = MutableStateFlow(false)
    val showMapProviderDialog: StateFlow<Boolean> = _showMapProviderDialog.asStateFlow()

    val experimentalSettingsEnabled: StateFlow<Boolean> = persistence.getExperimentalSettingsEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val trackOutagesEnabled: StateFlow<Boolean> = persistence.getTrackOutagesEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun openMapProviderDialog() {
        _showMapProviderDialog.value = true
    }

    fun closeMapProviderDialog() {
        _showMapProviderDialog.value = false
    }

    fun setCurrentMapProvider(provider: MapProviders) {
        viewModelScope.launch {
            mapProviderRepository.setCurrent(provider)
            closeMapProviderDialog()
        }
    }

    fun setExperimentalSettingsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            persistence.setExperimentalSettingsEnabled(enabled)
        }
    }

    fun setTrackOutagesEnabled(enabled: Boolean) {
        viewModelScope.launch {
            persistence.setTrackOutagesEnabled(enabled)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun formatLastDbUpdateDate(millis: Long): String {
        if (millis == 0L) return ""
        val instant = Instant.fromEpochMilliseconds(millis)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val format = LocalDateTime.Format {
            date(LocalDate.Formats.ISO)
            char(' ')
            time(LocalTime.Formats.ISO)
        }
        return dateTime.format(format)
    }

    fun openRepository() {
        appStoreUtils.openUrl("https://github.com/Alberto97/eGuasti")
    }

    fun openAppStoreForReview() {
        appStoreUtils.openForReview()
    }

}
