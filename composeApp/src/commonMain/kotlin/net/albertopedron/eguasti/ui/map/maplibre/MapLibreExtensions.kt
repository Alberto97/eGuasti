package net.albertopedron.eguasti.ui.map.maplibre

import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import net.albertopedron.eguasti.data.model.AppMapState
import net.albertopedron.eguasti.data.model.Outage
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position
import org.maplibre.spatialk.geojson.dsl.buildFeature

object MapLibreExtensions {
    const val OUTAGE_CAUSE_PROPERTY = "cause"

    fun List<Outage>.toGeoJson(): GeoJsonData {
        val features = map { outage ->
            buildFeature(
                geometry = Point(Position(outage.longitude, outage.latitude)),
                properties = buildJsonObject {
                    put(OUTAGE_CAUSE_PROPERTY, JsonPrimitive(outage.cause.name))
                }
            ) {
                id = JsonPrimitive(outage.id)
            }
        }
        return GeoJsonData.Features(FeatureCollection(features))
    }

    fun CameraPosition.toMapState(): AppMapState {
        return AppMapState(
            zoomLevel = zoom,
            longitude = target.longitude,
            latitude = target.latitude
        )
    }

    fun AppMapState.toCameraPosition(): CameraPosition {
        return CameraPosition(
            target = Position(longitude, latitude),
            zoom = zoomLevel
        )
    }
}