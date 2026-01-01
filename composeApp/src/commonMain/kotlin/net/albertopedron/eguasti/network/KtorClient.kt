package net.albertopedron.eguasti.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.protobuf.protobuf
import kotlinx.serialization.ExperimentalSerializationApi
import net.albertopedron.eguasti.network.KtorExtensions.xProtobuf

object KtorClient {
    @OptIn(ExperimentalSerializationApi::class)
    val client = HttpClient {
        install(ContentNegotiation) {
            protobuf(contentType = ContentType.Application.xProtobuf())
        }
    }
}
