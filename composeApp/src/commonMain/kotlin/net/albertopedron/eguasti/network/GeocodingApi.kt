package net.albertopedron.eguasti.network

import com.skydoves.sandwich.ApiResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import net.albertopedron.eguasti.network.model.json.CandidateCollection
import net.albertopedron.eguasti.network.model.json.SuggestionCollection

interface GeocodingApi {

    @GET("suggest")
    suspend fun suggest(
        @Query("text") text: String,
        @Query("maxSuggestions") maxSuggestions: Int = 6,
        @Query("f") f: String = "json"
    ): ApiResponse<SuggestionCollection>

    @GET("findAddressCandidates")
    suspend fun findAddressCandidates(
        @Query("magicKey") magicKey: String,
        @Query("maxLocations") maxLocations: Int = 6,
        @Query("outFields") outFields: String ="*",
        @Query("f") f: String = "json"
    ): ApiResponse<CandidateCollection>
}