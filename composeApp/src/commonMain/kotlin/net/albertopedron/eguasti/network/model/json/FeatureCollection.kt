package net.albertopedron.eguasti.network.model.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeatureCollection(
    val features: List<Feature>,
    val exceededTransferLimit: Boolean,
    val objectIdFieldName: String,
    val fields: List<Field>
)

@Serializable
data class Feature(
    val attributes: Attributes
)

@Serializable
data class Attributes(
    @SerialName("fid0") val fid0: Int? = null,
    val note: String,
    @SerialName("objectid1") val objectId1: Int,
    @SerialName("causa_interruzione") val interruptionCause: String,
    @SerialName("num_cli_disalim") val offlineCustomers: Int,
    @SerialName("dataultimoaggiornamento") val lastUpdate: String,
    @SerialName("descrizione_territoriale") val place: String,
    @SerialName("data_prev_ripristino") val expectedRestore: String,
    @SerialName("provincia") val province: String,
    @SerialName("cod_cs") val codCs: String,
    @SerialName("regione") val region: String,
    val cft: String,
    @SerialName("comune") val municipalityCode: Int,
    @SerialName("data_interruzione") val interruptionDate: String,
    @SerialName("latitudine") val latitude: Double,
    val x: String? = null,
    @SerialName("id_interruzione") val interruptionId: Long,
    val y: String? = null,
    @SerialName("causa_disalimentazione") val outageCause: OutageCause,
    @SerialName("fid2") val fid2: Int,
    @SerialName("longitudine") val longitude: Double,
    @SerialName("objectid") val objectId: Int? = null
)

@Serializable
enum class OutageCause {
    @SerialName("Guasto")
    FAILURE,
    @SerialName("Lavoro Programmato")
    MAINTENANCE;
}

@Serializable
data class Field(
    val visible: Boolean,
    val defaultValue: String? = null,
    val length: Int? = null,
    val type: String,
    val modelName: String,
    val name: String,
    val alias: String
)
