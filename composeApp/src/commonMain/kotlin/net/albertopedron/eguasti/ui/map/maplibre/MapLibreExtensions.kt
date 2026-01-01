package net.albertopedron.eguasti.ui.map.maplibre

import io.github.dellisd.spatialk.geojson.Point
import io.github.dellisd.spatialk.geojson.Position
import io.github.dellisd.spatialk.geojson.dsl.featureCollection
import net.albertopedron.eguasti.data.model.AppMapState
import net.albertopedron.eguasti.data.model.Outage
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.sources.GeoJsonData
import kotlin.collections.forEach

object MapLibreExtensions {
    const val OUTAGE_CAUSE_PROPERTY = "cause"

    fun List<Outage>.toGeoJson(): GeoJsonData {
        return GeoJsonData.Features(featureCollection {
            forEach { outage ->
                feature(
                    geometry = Point(Position(outage.longitude, outage.latitude)),
                    id = outage.id.toString()
                ) {
                    put(OUTAGE_CAUSE_PROPERTY, outage.cause.name)
                }
            }
        })
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