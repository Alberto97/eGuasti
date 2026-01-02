package net.albertopedron.eguasti.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import eguasti.composeapp.generated.resources.Res
import eguasti.composeapp.generated.resources.map_error_no_internet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.albertopedron.eguasti.data.MapProviderRepository
import net.albertopedron.eguasti.data.MapStateRepository
import net.albertopedron.eguasti.data.OutageRepository
import net.albertopedron.eguasti.data.OutageTracker
import net.albertopedron.eguasti.data.Persistence
import net.albertopedron.eguasti.data.TrackedOutage
import net.albertopedron.eguasti.data.model.AppMapState
import net.albertopedron.eguasti.data.model.Outage
import net.albertopedron.eguasti.domain.UpdateDatabaseUseCase
import net.albertopedron.eguasti.tools.DeviceConfigurationChanges
import net.albertopedron.eguasti.tools.NotificationHelper
import net.albertopedron.eguasti.tools.WorkScheduler
import org.jetbrains.compose.resources.getString


class MapViewModel(
    private val mapStateRepository: MapStateRepository = MapStateRepository(),
    private val outageRepository: OutageRepository = OutageRepository(),
    private val outageTracker: OutageTracker = OutageTracker(),
    private val workScheduler: WorkScheduler = WorkScheduler(),
    private val notificationHelper: NotificationHelper = NotificationHelper(),
    private val updateDatabaseUseCase: UpdateDatabaseUseCase = UpdateDatabaseUseCase(),
    private val mapProviderRepository: MapProviderRepository = MapProviderRepository(),
    private val persistence: Persistence = Persistence,
    private val deviceConfigurationChanges: DeviceConfigurationChanges = DeviceConfigurationChanges,
): ViewModel() {


    val mapProvider = mapProviderRepository.getCurrent().map { provider -> provider.getValue() }
    val outages = outageRepository.getAll()

    @OptIn(ExperimentalCoroutinesApi::class)
    val mapConfig = deviceConfigurationChanges.darkTheme.flatMapLatest {
        mapProviderRepository.getCurrentMapConfig(it)
    }

    private val _mapState: MutableSharedFlow<AppMapState> = MutableSharedFlow()
    val mapState = _mapState.asSharedFlow()

    private val _selectedOutage = MutableStateFlow<Outage?>(null)
    val selectedOutage = _selectedOutage.asStateFlow()

    private val _tracking = MutableStateFlow(false)
    val tracking = _tracking.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    private val _showPermissionDenied = MutableStateFlow<Boolean>(false)
    val showPermissionDenied = _showPermissionDenied.asStateFlow()

    val trackOutagesEnabled: StateFlow<Boolean> = persistence.getTrackOutagesEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            notificationHelper.initialize()
            loadMapPosition()
            loadOutages()
        }
    }

    private suspend fun loadMapPosition() {
        val state = mapStateRepository.readState() ?: return
        _mapState.emit(state)
    }

    fun saveMapPosition(state: AppMapState) {
        viewModelScope.launch {
            mapStateRepository.writeState(state)
        }
    }

    private suspend fun loadOutages() {
        Logger.e { "Loading outages" }
        val success = updateDatabaseUseCase.execute()
        if (!success) {
            _snackbarMessage.value = getString(Res.string.map_error_no_internet)
        }
    }

    fun toggleTrackOutage() {
        viewModelScope.launch {
            val isGranted = NotificationHelper.hasPermission()
            if (isGranted) {
                toggleTrackOutageInternal()
                return@launch
            }

            val granted = notificationHelper.requestPermission()
            if (granted)
                toggleTrackOutageInternal()
            else
                _showPermissionDenied.value = true
        }
    }

    private fun toggleTrackOutageInternal() {
        val outage = _selectedOutage.value ?: return
        if (tracking.value) {
            outageTracker.untrack(outage.id)
            _tracking.update { false }
        } else {
            val trackedOutage = TrackedOutage(outage.id, outage.lastUpdate)
            outageTracker.track(trackedOutage)
            workScheduler.schedule()
            _tracking.update { true }
        }
    }

    fun dismissPermissionDenied() {
        _showPermissionDenied.value = false
    }

    fun selectOutage(id: Int) {
        viewModelScope.launch {
            val outage = outageRepository.get(id) ?: return@launch
            _selectedOutage.update { outage }
            _tracking.update { outageTracker.isTracking(id) }
        }
    }

}