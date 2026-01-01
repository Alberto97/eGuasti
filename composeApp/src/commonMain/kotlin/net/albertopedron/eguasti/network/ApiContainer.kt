package net.albertopedron.eguasti.network

import com.skydoves.sandwich.ktorfit.ApiResponseConverterFactory
import de.jensklingenberg.ktorfit.Ktorfit


object ApiContainer {
    private const val URL = "https://ineuportalgis.enel.com/server/rest/services/Hosted/ITA_power_cut_map_layer_View/FeatureServer/0/"
    private val ktorfit = Ktorfit.Builder()
        .baseUrl(URL)
        .httpClient(KtorClient.client)
        .converterFactories(ApiResponseConverterFactory.create())
        .build()

    val enelApi = ktorfit.createEnelApi()
}
