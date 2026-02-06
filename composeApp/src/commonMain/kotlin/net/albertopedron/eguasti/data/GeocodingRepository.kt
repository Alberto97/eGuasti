package net.albertopedron.eguasti.data

import com.skydoves.sandwich.getOrNull
import net.albertopedron.eguasti.data.mapper.GeocodingMapper
import net.albertopedron.eguasti.data.model.GeocodingLocation
import net.albertopedron.eguasti.data.model.GeocodingSuggestion
import net.albertopedron.eguasti.network.ApiContainer
import net.albertopedron.eguasti.network.GeocodingApi

class GeocodingRepository(
    private val client: GeocodingApi = ApiContainer.geocodingApi,
    private val mapper: GeocodingMapper = GeocodingMapper()
) {

    suspend fun getSuggestions(value: String): Result<List<GeocodingSuggestion>> {
        val result = client.suggest(text = value)
        val value = result.getOrNull() ?: return Result.failure(Exception("Query failed"))
        val suggestions = value.suggestions.map { mapper.mapSuggestion(it) }
        return Result.success(suggestions)
    }

    suspend fun getLocation(key: String): Result<GeocodingLocation> {
        val result = client.findAddressCandidates(magicKey = key)
        val value = result.getOrNull() ?: return Result.failure(Exception("Query failed"))
        val location = value.candidates.firstOrNull()?.location ?: return Result.failure(Exception("No location found"))
        val loc = mapper.mapLocation(location)
        return Result.success(loc)
    }
}
