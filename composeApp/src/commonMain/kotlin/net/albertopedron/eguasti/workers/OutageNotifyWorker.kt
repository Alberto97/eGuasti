package net.albertopedron.eguasti.workers

import eguasti.composeapp.generated.resources.Res
import eguasti.composeapp.generated.resources.track_outage_notification_message
import io.ktor.client.request.invoke
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import net.albertopedron.eguasti.data.OutageRepository
import net.albertopedron.eguasti.data.OutageTracker
import net.albertopedron.eguasti.data.TrackedOutage
import net.albertopedron.eguasti.data.model.Outage
import net.albertopedron.eguasti.tools.NotificationHelper
import net.albertopedron.eguasti.tools.WorkScheduler
import org.jetbrains.compose.resources.getString

class OutageNotifyTask {
    private val repository = OutageRepository()
    private val tracker = OutageTracker()
    private val scheduler = WorkScheduler()
    private val notificationHelper = NotificationHelper()


    suspend fun start(): Boolean = withContext(Dispatchers.IO) {
        val result = repository.fetch()
        return@withContext if (result.isSuccess) {
            processOutages(result.getOrNull() ?: emptyList())
            true
        } else {
            false
        }
    }

    private suspend fun processOutages(outages: List<Outage>) {
        val tracked = tracker.getTracked()
        val ongoingTracked = OutageListHelper.findTracked(tracked, outages)

        notify(ongoingTracked)
        untrackResolved(tracked, ongoingTracked)
        unscheduleIfEmpty(ongoingTracked)
    }

    private suspend fun notify(ongoingTracked: List<Outage>) {
        ongoingTracked.forEach { outage ->
            // TODO: Check last update to make sure we do not notify the same status twice
            val expectedRestore = outage.expectedRestore
            val message = getString(Res.string.track_outage_notification_message, expectedRestore)
            notificationHelper.postNotification(outage.id, outage.place, message)
        }
    }

    private fun untrackResolved(tracked: List<TrackedOutage>, ongoingTracked: List<Outage>) {
        val toUntrack = OutageListHelper.findTrackedResolved(tracked, ongoingTracked)
        toUntrack.forEach { id -> tracker.untrack(id.id) }
    }

    private fun unscheduleIfEmpty(ongoingTracked: List<Outage>) {
        if (ongoingTracked.isEmpty()) {
            scheduler.unschedule()
        }
    }
}

object OutageListHelper {
    fun findTracked(trackedIds: List<TrackedOutage>, outages: List<Outage>): List<Outage> {
        return trackedIds.mapNotNull { tracked -> findTrackedOutage(tracked.id, outages) }
    }

    private fun findTrackedOutage(trackedId: Int, outages: List<Outage>): Outage? {
        return outages.firstOrNull { it.id == trackedId }
    }

    fun findTrackedResolved(trackedIds: List<TrackedOutage>, ongoingOutages: List<Outage>): List<TrackedOutage> {
        val ongoingIds = ongoingOutages.map { it.id }
        return trackedIds.filterNot { it.id in ongoingIds }
    }
}

