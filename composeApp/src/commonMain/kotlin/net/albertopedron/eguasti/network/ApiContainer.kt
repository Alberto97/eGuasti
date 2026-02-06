package net.albertopedron.eguasti.network

import com.skydoves.sandwich.ktorfit.ApiResponseConverterFactory
import de.jensklingenberg.ktorfit.Ktorfit


object ApiContainer {
    private const val URL = "https://ineuportalgis.enel.com/server/rest/services/Hosted/ITA_power_cut_map_layer_View/FeatureServer/0/"
    private const val GEOCODING_URL = "https://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/"

    private val ktorfit = Ktorfit.Builder()
        .baseUrl(URL)
        .httpClient(KtorClient.client)
        .converterFactories(ApiResponseConverterFactory.create())
        .build()

    private val geocodingKtorfit = Ktorfit.Builder()
        .baseUrl(GEOCODING_URL)
        .httpClient(KtorClient.client)
        .converterFactories(ApiResponseConverterFactory.create())
        .build()

    val enelApi = ktorfit.createEnelApi()
    val geocodingApi = geocodingKtorfit.createGeocodingApi()
}
