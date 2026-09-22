package net.albertopedron.eguasti.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.CertificatePinner

actual fun createHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(OkHttp) {
        engine {
            config {
                val builder = CertificatePinner.Builder()
                    .add("dpa-portalgis.enel.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")

                certificatePinner(builder.build())
            }
        }
        apply(block)
    }
}