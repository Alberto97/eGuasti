package net.albertopedron.eguasti.network

import io.ktor.http.ContentType

object KtorExtensions {
    fun ContentType.Application.xProtobuf() = ContentType(TYPE, "x-protobuf")
}