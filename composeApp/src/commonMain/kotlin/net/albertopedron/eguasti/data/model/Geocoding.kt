package net.albertopedron.eguasti.data.model

data class GeocodingSuggestion(
    val text: String,
    val key: String
)

data class GeocodingLocation(
    val latitude: Double,
    val longitude: Double
)
