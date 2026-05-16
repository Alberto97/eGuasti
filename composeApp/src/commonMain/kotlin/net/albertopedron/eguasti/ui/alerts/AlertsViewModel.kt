package net.albertopedron.eguasti.ui.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.albertopedron.eguasti.data.OutageRepository
import net.albertopedron.eguasti.data.OutageTracker
import net.albertopedron.eguasti.data.model.Outage
import net.albertopedron.eguasti.tools.WorkScheduler

class AlertsViewModel(
    private val outageTracker: OutageTracker = OutageTracker(),
    private val outageRepository: OutageRepository = OutageRepository(),
    private val workScheduler: WorkScheduler = WorkScheduler(),
) : ViewModel() {

    private val _trackedIds = MutableStateFlow(
        outageTracker.getTracked().map { it.id }.toSet()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val alerts: StateFlow<List<Alert>> = _trackedIds
        .flatMapLatest { ids ->
            outageRepository.getAll().map { outages ->
                outages
                    .filter { it.id in ids }
                    .map { it.toAlert() }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun dismiss(alert: Alert) {
        outageTracker.untrack(alert.id)
        val newIds = _trackedIds.value - alert.id
        _trackedIds.value = newIds
        if (newIds.isEmpty()) {
            workScheduler.unschedule()
        }
    }

    private fun Outage.toAlert(): Alert = Alert(
        id = id,
        place = place,
        expectedRestore = expectedRestore,
        cause = cause,
    )
}
