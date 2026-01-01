package net.albertopedron.eguasti.data.mapper

import net.albertopedron.eguasti.data.model.Cause
import net.albertopedron.eguasti.data.model.Outage
import net.albertopedron.eguasti.database.OutageEntity
import net.albertopedron.eguasti.network.model.json.Attributes
import net.albertopedron.eguasti.network.model.json.FeatureCollection
import net.albertopedron.eguasti.network.model.json.OutageCause
import net.albertopedron.eguasti.network.model.pbf.FeatureCollectionPBuffer

class OutageMapper {
    fun mapFeatures(collection: FeatureCollection): List<Outage> {
        return collection.features.map { feature -> fromAttributes(feature.attributes) }
    }

    private fun fromAttributes(attributes: Attributes): Outage {
        return Outage(
            id = attributes.interruptionId.toInt(),
            start = attributes.interruptionDate,
            expectedRestore = attributes.expectedRestore,
            lastUpdate = attributes.lastUpdate,
            place = attributes.place,
            latitude = attributes.latitude,
            longitude = attributes.longitude,
            offlineCustomers = attributes.offlineCustomers,
            cause = mapCause(attributes.outageCause)
        )
    }

    private fun mapCause(outageCause: OutageCause): Cause = when (outageCause) {
        OutageCause.FAILURE -> Cause.FAILURE
        OutageCause.MAINTENANCE -> Cause.MAINTENANCE
    }

    fun mapFeatures(featureResult: FeatureCollectionPBuffer.FeatureResult): List<Outage> {
        return featureResult.features.map { feature ->
            mapFeatureToOutage(feature, featureResult.fields)
        }
    }

    private fun mapFeatureToOutage(
        feature: FeatureCollectionPBuffer.Feature,
        fields: List<FeatureCollectionPBuffer.Field>
    ): Outage {
        val attrMap = fields.mapIndexedNotNull { index, field ->
            val attribute = feature.attributes.getOrNull(index) ?: return@mapIndexedNotNull null
            field.name to mapFieldToAttribute(field, attribute)
        }.toMap()

        return Outage(
            id = attrMap["id_interruzione"] as? Int ?: 0,
            start = attrMap["data_interruzione"] as? String ?: "",
            expectedRestore = attrMap["data_prev_ripristino"] as? String ?: "",
            lastUpdate = attrMap["dataultimoaggiornamento"] as? String ?: "",
            place = attrMap["descrizione_territoriale"] as? String ?: "",
            latitude = attrMap["latitudine"] as? Double ?: 0.0,
            longitude = attrMap["longitudine"] as? Double ?: 0.0,
            offlineCustomers = attrMap["num_cli_disalim"] as? Int ?: 0,
            cause = when (attrMap["causa_disalimentazione"] as? String ?: "") {
                "Guasto" -> Cause.FAILURE
                "Lavoro Programmato" -> Cause.MAINTENANCE
                else -> Cause.FAILURE
            }
        )
    }

    private fun mapFieldToAttribute(field: FeatureCollectionPBuffer.Field, value: FeatureCollectionPBuffer.Value): Any? {
        return when (field.fieldType) {
            FeatureCollectionPBuffer.FieldType.esriFieldTypeInteger -> value.sintValue
            FeatureCollectionPBuffer.FieldType.esriFieldTypeDouble -> value.doubleValue
            FeatureCollectionPBuffer.FieldType.esriFieldTypeString -> value.stringValue
            else -> null
        }
    }

    fun toEntity(value: Outage): OutageEntity {
        return OutageEntity(
            id = value.id,
            start = value.start,
            expectedRestore = value.expectedRestore,
            lastUpdate = value.lastUpdate,
            place = value.place,
            latitude = value.latitude,
            longitude = value.longitude,
            offlineCustomers = value.offlineCustomers,
            cause = value.cause.name
        )
    }

    fun fromEntity(value: OutageEntity): Outage {
        return Outage(
            id = value.id,
            start = value.start,
            expectedRestore = value.expectedRestore,
            lastUpdate = value.lastUpdate,
            place = value.place,
            latitude = value.latitude,
            longitude = value.longitude,
            offlineCustomers = value.offlineCustomers,
            cause = Cause.valueOf(value.cause)
        )
    }
}