package net.albertopedron.eguasti.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.serialization.kotlinx.protobuf.protobuf
import kotlinx.serialization.ExperimentalSerializationApi
import net.albertopedron.eguasti.network.KtorExtensions.xProtobuf

expect fun createHttpClient(block: HttpClientConfig<*>.() -> Unit = {}): HttpClient

object KtorClient {
    @OptIn(ExperimentalSerializationApi::class)
    val client = createHttpClient {
        install(ContentNegotiation) {
            json()
            protobuf(contentType = ContentType.Application.xProtobuf())
        }
    }
}
