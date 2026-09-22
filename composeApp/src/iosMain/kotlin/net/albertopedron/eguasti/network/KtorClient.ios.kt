package net.albertopedron.eguasti.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.engine.darwin.certificates.CertificatePinner

actual fun createHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(Darwin) {
        engine {
            val builder = CertificatePinner.Builder()
                .add("dpa-portalgis.enel.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")

            handleChallenge(builder.build())
        }
        apply(block)
    }
}