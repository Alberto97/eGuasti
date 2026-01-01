package net.albertopedron.eguasti.tools


expect class NotificationHelper() {

    companion object {
        suspend fun hasPermission(): Boolean
    }

    suspend fun initialize()

    fun postNotification(id: Int, title: String, text: String)

    suspend fun requestPermission(): Boolean

}