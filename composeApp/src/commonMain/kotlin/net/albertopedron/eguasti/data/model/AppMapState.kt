package net.albertopedron.eguasti.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppMapState(
    val zoomLevel: Double,
    val latitude: Double,
    val longitude: Double
)