package net.albertopedron.eguasti.data.model


data class Outage(
    val id: Int,
    val start: String,
    val expectedRestore: String,
    val lastUpdate: String,
    val place: String,
    val latitude: Double,
    val longitude: Double,
    val offlineCustomers: Int,
    val cause: Cause
)

enum class Cause {
    FAILURE,
    MAINTENANCE;
}

fun Cause.toSql(): String {
    return when (this) {
        Cause.FAILURE -> "Guasto"
        Cause.MAINTENANCE -> "Lavoro Programmato"
    }
}