package net.albertopedron.eguasti.data.mapper

import net.albertopedron.eguasti.data.model.GeocodingLocation
import net.albertopedron.eguasti.data.model.GeocodingSuggestion
import net.albertopedron.eguasti.network.model.json.Candidate
import net.albertopedron.eguasti.network.model.json.Suggestion

class GeocodingMapper {
    fun mapSuggestion(value: Suggestion): GeocodingSuggestion {
        return GeocodingSuggestion(
            text = value.text,
            key = value.magicKey
        )
    }

    fun mapLocation(value: Candidate.Location): GeocodingLocation {
        return GeocodingLocation(
            latitude = value.y,
            longitude = value.x
        )
    }
}