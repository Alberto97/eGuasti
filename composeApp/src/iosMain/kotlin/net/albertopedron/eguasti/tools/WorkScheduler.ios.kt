package net.albertopedron.eguasti.tools

import kotlinx.cinterop.*
import platform.BackgroundTasks.BGAppRefreshTaskRequest
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSinceNow

actual class WorkScheduler actual constructor() {

    companion object {
        const val OUTAGE_NOTIFY_TASK_UNIQUE_NAME = "outageNotify"
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun schedule() {
        val request = BGAppRefreshTaskRequest(
            identifier = OUTAGE_NOTIFY_TASK_UNIQUE_NAME
        ).apply {
            earliestBeginDate = NSDate.dateWithTimeIntervalSinceNow(15.0 * 60.0) // 15 min minimum
        }

        val success = BGTaskScheduler.sharedScheduler.submitTaskRequest(request, null)

        if (!success) {
            println("Failed to submit BG task")
        }
    }

    actual fun unschedule() {
        BGTaskScheduler.sharedScheduler.cancelTaskRequestWithIdentifier(OUTAGE_NOTIFY_TASK_UNIQUE_NAME)
    }
}