package net.albertopedron.eguasti.ui.map.maplibre

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eguasti.composeapp.generated.resources.Res
import eguasti.composeapp.generated.resources.marker_red
import eguasti.composeapp.generated.resources.marker_yellow
import net.albertopedron.eguasti.data.model.AppMapState
import net.albertopedron.eguasti.data.model.Cause
import net.albertopedron.eguasti.data.model.Outage
import net.albertopedron.eguasti.ui.map.maplibre.MapLibreExtensions.OUTAGE_CAUSE_PROPERTY
import net.albertopedron.eguasti.ui.map.maplibre.MapLibreExtensions.toCameraPosition
import net.albertopedron.eguasti.ui.map.maplibre.MapLibreExtensions.toGeoJson
import net.albertopedron.eguasti.ui.map.maplibre.MapLibreExtensions.toMapState
import net.albertopedron.eguasti.ui.theme.Blue
import org.jetbrains.compose.resources.painterResource
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.asNumber
import org.maplibre.compose.expressions.dsl.asString
import org.maplibre.compose.expressions.dsl.case
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.not
import org.maplibre.compose.expressions.dsl.step
import org.maplibre.compose.expressions.dsl.switch
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.sources.GeoJsonOptions
import org.maplibre.compose.sources.GeoJsonSource
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

val defaultPosition = CameraPosition(
    target = Position(12.4964, 41.9028),
    zoom = 5.0
)

@Composable
fun MapLibreMap(
    styleUri: String,
    mapState: AppMapState?,
    saveMapPosition: (AppMapState) -> Unit,
    outages: List<Outage>,
    onOutageClicked: (Int) -> Unit,
    clearOutageSelection: () -> Unit,
) {
    val cameraState = rememberCameraState(firstPosition = defaultPosition)
    val mapLoaded = remember { mutableStateOf(false) }
    val center = remember { mutableStateOf<CameraPosition?>(null) }

    LaunchedEffect(center.value) {
        val position = center.value ?: return@LaunchedEffect
        cameraState.animateTo(position)
    }

    LaunchedEffect(mapLoaded.value) {
        val position = mapState ?: return@LaunchedEffect
        cameraState.animateTo(position.toCameraPosition())
    }

    MapLibreMap(
        styleUri = styleUri,
        outages = outages,
        cameraState = cameraState,
        onOutageClicked = {
            onOutageClicked(it)
            saveMapPosition(cameraState.position.toMapState())
        },
        clearOutageSelection = {
            clearOutageSelection()
        },
        centerCameraTo = { point, zoom ->
            val position = cameraState.position.copy(
                target = point.coordinates,
                zoom = zoom ?: cameraState.position.zoom,
            )
            center.value = position
        },
        onMapLoadFinished = {
            mapLoaded.value = true
        }
    )
}

@Composable
private fun MapLibreMap(
    styleUri: String,
    outages: List<Outage>,
    cameraState: CameraState,
    onOutageClicked: (Int) -> Unit,
    clearOutageSelection: () -> Unit,
    centerCameraTo: (point: Point, zoom: Double?) -> Unit,
    onMapLoadFinished: () -> Unit = {},
) {
    MaplibreMap(
        modifier = Modifier.fillMaxSize(),
        baseStyle = BaseStyle.Uri(styleUri),
        cameraState = cameraState,
        options = MapOptions(
            ornamentOptions = OrnamentOptions(
                padding = WindowInsets.navigationBars.asPaddingValues(),
            )
        ),
        onMapClick = { _, _ ->
            clearOutageSelection()
            ClickResult.Pass
        },
        onMapLoadFinished = onMapLoadFinished
    ) {

        val outageSource = rememberGeoJsonSource(
            data = outages.toGeoJson(),
            options = GeoJsonOptions(
                minZoom = 3,
                cluster = true,
            ),
        )

        ClusteredLayer(
            outageSource = outageSource,
            zoomToCluster = { point ->
                val zoom = (cameraState.position.zoom + 2).coerceAtMost(20.0)
                centerCameraTo(point, zoom)
            }
        )

        UnclusteredLayer(
            outageSource = outageSource,
            centerCameraTo = { centerCameraTo(it, null) },
            onOutageClicked = { id ->
                id ?: return@UnclusteredLayer
                onOutageClicked(id)
            }
        )

    }
}

@Composable
private fun ClusteredLayer(
    outageSource: GeoJsonSource,
    zoomToCluster: (Point) -> Unit
) {
    CircleLayer(
        id = "clustered-outages",
        source = outageSource,
        filter = feature.has("point_count"),
        color = const(Blue),
        opacity = const(0.7f),
        strokeWidth = const(3.dp),
        strokeColor = const(Color.White.copy(alpha = 0.5f)),
        radius =
            step(
                input = feature["point_count"].asNumber(),
                fallback = const(20.dp),
                25 to const(20.dp),
                100 to const(30.dp),
                500 to const(40.dp),
                1000 to const(50.dp),
                5000 to const(60.dp),
            ),
        onClick = { features ->
            features.firstOrNull()?.geometry?.let {
                zoomToCluster(it as Point)
                ClickResult.Consume
            } ?: ClickResult.Pass
        },
    )

    SymbolLayer(
        id = "clustered-outages-count",
        source = outageSource,
        filter = feature.has("point_count"),
        textField = feature["point_count_abbreviated"].asString(),
        textFont = const(listOf("Noto Sans Regular")),
        textColor = const(Color.White),
    )
}

@Composable
private fun UnclusteredLayer(
    outageSource: GeoJsonSource,
    centerCameraTo: (Point) -> Unit,
    onOutageClicked: (Int?) -> Unit
) {
    SymbolLayer(
        id = "unclustered-outages",
        source = outageSource,
        filter = !feature.has("point_count"),
        iconImage = switch(
            input = feature[OUTAGE_CAUSE_PROPERTY].asString(),
            case(
                label = Cause.MAINTENANCE.name,
                output = image(painterResource(Res.drawable.marker_yellow))
            ),
            case(
                label = Cause.FAILURE.name,
                output = image(painterResource(Res.drawable.marker_red))
            ),
            fallback = image(painterResource(Res.drawable.marker_red))
        ),
        onClick = { features ->
            val feature = features.firstOrNull() ?: return@SymbolLayer ClickResult.Pass
            centerCameraTo(feature.geometry as Point)

            val id = feature.id?.content?.toInt() ?: return@SymbolLayer ClickResult.Pass
            onOutageClicked(id)

            ClickResult.Consume
        }
    )
}