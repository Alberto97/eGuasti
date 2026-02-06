@file:OptIn(ExperimentalSerializationApi::class)

package net.albertopedron.eguasti.network.model.json

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
data class CandidateCollection(
    val candidates: List<Candidate>
)

@Serializable
@JsonIgnoreUnknownKeys
data class Candidate(
    val address: String,
    val location: Location,
    val attributes: Map<String, String>,
) {
    @Serializable
    data class Location(
        val x: Double,
        val y: Double,
    )
}

@Serializable
data class SuggestionCollection(
    val suggestions: List<Suggestion>
)

@Serializable
@JsonIgnoreUnknownKeys
data class Suggestion(
    val magicKey: String,
    val text: String,
)
