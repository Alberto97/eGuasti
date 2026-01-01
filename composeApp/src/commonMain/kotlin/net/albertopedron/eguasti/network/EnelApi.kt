package net.albertopedron.eguasti.network

import com.skydoves.sandwich.ApiResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import net.albertopedron.eguasti.network.model.json.FeatureCollection
import net.albertopedron.eguasti.network.model.pbf.FeatureCollectionPBuffer

interface EnelApi {
    @GET("query")
    suspend fun queryPbf(
        @Query("where") where: String,
        @Query("f") format: String = "pbf",
        @Query("returnGeometry") returnGeometry: Boolean = false,
        @Query("outFields") outFields: String = "*"
    ): ApiResponse<FeatureCollectionPBuffer>

    @GET("query")
    suspend fun queryJson(
        @Query("where") where: String,
        @Query("f") format: String = "json",
        @Query("returnGeometry") returnGeometry: Boolean = false,
        @Query("outFields") outFields: String = "*"
    ): ApiResponse<FeatureCollection>
}
