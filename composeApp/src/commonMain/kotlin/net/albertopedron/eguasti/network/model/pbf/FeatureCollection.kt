@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("EnumEntryName", "unused", "ArrayInDataClass")

package net.albertopedron.eguasti.network.model.pbf

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class FeatureCollectionPBuffer(
    //@ProtoNumber(1) val version: String,
    @ProtoNumber(2) val queryResult: QueryResult
) {
    @Serializable
    enum class GeometryType {
        esriGeometryTypePoint,
        esriGeometryTypeMultipoint,
        esriGeometryTypePolyline,
        esriGeometryTypePolygon,
        esriGeometryTypeMultipatch,
        esriGeometryTypeNone,
        esriGeometryTypeEnvelope
    }

    @Serializable
    enum class FieldType {
        esriFieldTypeSmallInteger,
        esriFieldTypeInteger,
        esriFieldTypeSingle,
        esriFieldTypeDouble,
        esriFieldTypeString,
        esriFieldTypeDate,
        esriFieldTypeOID,
        esriFieldTypeGeometry,
        esriFieldTypeBlob,
        esriFieldTypeRaster,
        esriFieldTypeGUID,
        esriFieldTypeGlobalID,
        esriFieldTypeXML,
        esriFieldTypeBigInteger,
        esriFieldTypeDateOnly,
        esriFieldTypeTimeOnly,
        esriFieldTypeTimestampOffset
    }

    @Serializable
    enum class SQLType {
        sqlTypeBigInt,
        sqlTypeBinary,
        sqlTypeBit,
        sqlTypeChar,
        sqlTypeDate,
        sqlTypeDecimal,
        sqlTypeDouble,
        sqlTypeFloat,
        sqlTypeGeometry,
        sqlTypeGUID,
        sqlTypeInteger,
        sqlTypeLongNVarchar,
        sqlTypeLongVarbinary,
        sqlTypeLongVarchar,
        sqlTypeNChar,
        sqlTypeNVarchar,
        sqlTypeOther,
        sqlTypeReal,
        sqlTypeSmallInt,
        sqlTypeSqlXml,
        sqlTypeTime,
        sqlTypeTimestamp,
        sqlTypeTimestamp2,
        sqlTypeTinyInt,
        sqlTypeVarbinary,
        sqlTypeVarchar,
        sqlTypeTimestampWithTimezone
    }

    @Serializable
    enum class QuantizeOriginPostion { upperLeft, lowerLeft }

    @Serializable
    enum class SegmentType { line, arc, bezier, ellipticArc }

    @Serializable
    data class SpatialReference(
        @ProtoNumber(1) val wkid: UInt,
        @ProtoNumber(2) val lastestWkid: UInt,
        @ProtoNumber(3) val vcsWkid: UInt,
        @ProtoNumber(4) val latestVcsWkid: UInt,
        @ProtoNumber(5) val wkt: String,
        @ProtoNumber(6) val wkt2: String
    )

    @Serializable
    data class Field(
        @ProtoNumber(1) val name: String,
        @ProtoNumber(2) val fieldType: FieldType,
        @ProtoNumber(3) val alias: String,
        @ProtoNumber(4) val sqlType: SQLType,
//        @ProtoNumber(5) val domain: String,
//        @ProtoNumber(6) val defaultValue: String
    )

    @Serializable
    data class GeometryField(
        @ProtoNumber(1) val field: Field,
        @ProtoNumber(2) val geometryType: GeometryType
    )

    @Serializable
    data class Envelope(
        @ProtoNumber(1) val XMin: Double,
        @ProtoNumber(2) val YMin: Double,
        @ProtoNumber(3) val XMax: Double,
        @ProtoNumber(4) val YMax: Double,
        @ProtoNumber(5) val spatialReference: SpatialReference
    )

    @Serializable
    data class Value(
        @ProtoNumber(1) val stringValue: String? = null,
        @ProtoNumber(2) val floatValue: Float? = null,
        @ProtoNumber(3) val doubleValue: Double? = null,
        @ProtoNumber(4) val sintValue: Int? = null,
        @ProtoNumber(5) val uintValue: UInt? = null,
        @ProtoNumber(6) val int64Value: Long? = null,
        @ProtoNumber(7) val uint64Value: ULong? = null,
        @ProtoNumber(8) val sint64Value: Long? = null,
        @ProtoNumber(9) val boolValue: Boolean? = null,
        @ProtoNumber(10) val nullValue: Boolean? = null,
        @ProtoNumber(11) val index: UInt? = null
    )

    @Serializable
    data class Geometry(
        @ProtoNumber(1) val geometryType: GeometryType,
        @ProtoNumber(2) val lengths: List<UInt>,
        @ProtoNumber(3) val coords: List<Long>,
        @ProtoNumber(4) val ids: List<Int>
    )

    @Serializable
    data class CurveGeometry(
        @ProtoNumber(1) val geometryType: GeometryType,
        @ProtoNumber(2) val parts: List<UInt>,
        @ProtoNumber(3) val segmentSets: List<SegmentSet>,
        @ProtoNumber(4) val coords: List<Long>
    )

    @Serializable
    data class SegmentSet(
        @ProtoNumber(1) val type: SegmentType,
        @ProtoNumber(2) val count: UInt,
        @ProtoNumber(3) val parameters: List<Double>
    )

    @Serializable
    data class EsriShapeBuffer(@ProtoNumber(1) val bytes: ByteArray)

    @Serializable
    data class Feature(
        @ProtoNumber(1) val attributes: List<Value>,
        @ProtoNumber(2) val geometry: Geometry? = null,
        @ProtoNumber(3) val shapeBuffer: EsriShapeBuffer? = null,
        @ProtoNumber(7) val curveGeometry: CurveGeometry? = null,
        @ProtoNumber(4) val centroid: Geometry? = null,
        @ProtoNumber(5) val aggregateGeometries: List<Geometry> = emptyList(),
        @ProtoNumber(6) val envelope: Envelope? = null
    )

    @Serializable
    data class UniqueIdField(
        @ProtoNumber(1) val name: String,
        @ProtoNumber(2) val isSystemMaintained: Boolean
    )

    @Serializable
    data class GeometryProperties(
        @ProtoNumber(1) val shapeAreaFieldName: String,
        @ProtoNumber(2) val shapeLengthFieldName: String,
        @ProtoNumber(3) val units: String
    )

    @Serializable
    data class ServerGens(
        @ProtoNumber(1) val minServerGen: ULong,
        @ProtoNumber(2) val serverGen: ULong
    )

    @Serializable
    data class Scale(
        @ProtoNumber(1) val xScale: Double,
        @ProtoNumber(2) val yScale: Double,
        @ProtoNumber(3) val mScale: Double,
        @ProtoNumber(4) val zScale: Double
    )

    @Serializable
    data class Translate(
        @ProtoNumber(1) val xTranslate: Double,
        @ProtoNumber(2) val yTranslate: Double,
        @ProtoNumber(3) val mTranslate: Double,
        @ProtoNumber(4) val zTranslate: Double
    )

    @Serializable
    data class Transform(
        @ProtoNumber(1) val quantizeOriginPostion: QuantizeOriginPostion,
        @ProtoNumber(2) val scale: Scale,
        @ProtoNumber(3) val translate: Translate
    )

    @Serializable
    data class FeatureResult(
        @ProtoNumber(1) val objectIdFieldName: String,
        //@ProtoNumber(2) val uniqueIdField: UniqueIdField,
        //@ProtoNumber(3) val globalIdFieldName: String,
        //@ProtoNumber(4) val geohashFieldName: String,
        //@ProtoNumber(5) val geometryProperties: GeometryProperties,
        //@ProtoNumber(6) val serverGens: ServerGens,
        //@ProtoNumber(7) val geometryType: GeometryType,
        //@ProtoNumber(8) val spatialReference: SpatialReference,
        //@ProtoNumber(9) val exceededTransferLimit: Boolean,
        //@ProtoNumber(10) val hasZ: Boolean,
        //@ProtoNumber(11) val hasM: Boolean,
        //@ProtoNumber(12) val transform: Transform,
        @ProtoNumber(13) val fields: List<Field>,
        @ProtoNumber(14) val values: List<Value>,
        @ProtoNumber(15) val features: List<Feature>,
        @ProtoNumber(16) val geometryFields: List<GeometryField>
    )

    @Serializable
    data class CountResult(@ProtoNumber(1) val count: ULong)

    @Serializable
    data class ObjectIdsResult(
        @ProtoNumber(1) val objectIdFieldName: String,
        @ProtoNumber(2) val serverGens: ServerGens,
        @ProtoNumber(3) val objectIds: List<ULong>
    )

    @Serializable
    data class ExtentCountResult(
        @ProtoNumber(1) val extent: Envelope,
        @ProtoNumber(2) val count: ULong? = null
    )

    @Serializable
    data class QueryResult(
        @ProtoNumber(1) val featureResult: FeatureResult? = null,
        @ProtoNumber(2) val countResult: CountResult? = null,
        @ProtoNumber(3) val idsResult: ObjectIdsResult? = null,
        @ProtoNumber(4) val extentCountResult: ExtentCountResult? = null
    )
}
