package net.albertopedron.eguasti.ui.map.maplibre

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eguasti.composeapp.generated.resources.Res
import eguasti.composeapp.generated.resources.marker_red
import eguasti.composeapp.generated.resources.marker_yellow
import net.albertopedron.eguasti.data.MapConfig
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
import org.maplibre.compose.expressions.dsl.asNumber
import org.maplibre.compose.expressions.dsl.asString
import org.maplibre.compose.expressions.dsl.case
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.feature
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.not
import org.maplibre.compose.expressions.dsl.step
import org.maplibre.compose.expressions.dsl.switch
import org.maplibre.compose.interaction.ClickResult
import org.maplibre.compose.interaction.MapInteractions
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.RasterLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MapEvent
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.sources.GeoJsonOptions
import org.maplibre.compose.sources.GeoJsonSource
import org.maplibre.compose.sources.TileSetOptions
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.sources.rememberRasterTileSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position
import kotlin.time.Clock

val defaultPosition = CameraPosition(
    target = Position(12.4964, 41.9028),
    zoom = 5.0
)

@Composable
fun MapLibreMap(
    mapConfig: MapConfig,
    mapState: AppMapState?,
    saveMapPosition: (AppMapState) -> Unit,
    outages: List<Outage>,
    onOutageClicked: (Int) -> Unit,
    clearOutageSelection: () -> Unit,
) {
    MapLibreMap(
        mapConfig = mapConfig,
        mapState = mapState,
        outages = outages,
        onOutageClicked = { id, position ->
            onOutageClicked(id)
            saveMapPosition(position.toMapState())
        },
        clearOutageSelection = {
            clearOutageSelection()
        },
    )
}

@Composable
private fun MapLibreMap(
    mapConfig: MapConfig,
    mapState: AppMapState?,
    outages: List<Outage>,
    onOutageClicked: (Int, CameraPosition) -> Unit,
    clearOutageSelection: () -> Unit,
) {
    // For some reason symbol layers (eg. cluster layers) are not rendered if a valid base
    // style is not specified when using raster tiles
    val baseStyle = when (mapConfig) {
        is MapConfig.Raster -> BaseStyle.Demo // Does not work with BaseStyle.Empty
        is MapConfig.Vector -> BaseStyle.Uri(mapConfig.uri)
    }

    val mapLoaded = remember { mutableStateOf(false) }
    val requestZoomTo = remember { mutableStateOf<Point?>(null) }
    val requestCenterTo = remember { mutableStateOf<Point?>(null) }
    val requestClickId = remember { mutableStateOf<Pair<Int, Long>?>(null) }
    val center = remember { mutableStateOf<CameraPosition?>(null) }

    val state = rememberMapState(
        baseStyle = baseStyle,
        initialCameraPosition = defaultPosition
    ) {
        if (mapConfig is MapConfig.Raster) {
            RasterMapLayer(mapConfig)
        }

        val outageSource = rememberGeoJsonSource(
            data = outages.toGeoJson(),
            options = GeoJsonOptions(
                minZoom = 3,
                cluster = true,
            ),
        )

        ClusteredLayer(
            outageSource = outageSource,
            zoomToCluster = { point -> requestZoomTo.value = point }
        )

        UnclusteredLayer(
            outageSource = outageSource,
            centerCameraTo = { requestCenterTo.value = it },
            onOutageClicked = { id ->
                // Use current millis to handle reselection that would not trigger recomposition since the id is always the same
                requestClickId.value = Pair(id, Clock.System.now().toEpochMilliseconds())
            }
        )

    }

    SideEffect(requestClickId.value) {
        val id = requestClickId.value ?: return@SideEffect
        onOutageClicked(id.first, state.cameraPosition)
    }

    fun centerCameraTo(point: Point, zoom: Double?) {
        val position = state.cameraPosition.copy(
            target = point.coordinates,
            zoom = zoom ?: state.cameraPosition.zoom,
        )
        center.value = position
    }

    SideEffect(state, requestZoomTo.value) {
        val point = requestZoomTo.value ?: return@SideEffect
        val zoom = (state.cameraPosition.zoom + 2).coerceAtMost(20.0)
        centerCameraTo(point, zoom)
    }

    SideEffect( requestCenterTo.value) {
        val point = requestCenterTo.value ?: return@SideEffect
        centerCameraTo(point, null)
    }


    LaunchedEffect(center.value) {
        val position = center.value ?: return@LaunchedEffect
        state.animateCameraPosition(position)
    }

    LaunchedEffect(mapLoaded.value) {
        val position = mapState ?: return@LaunchedEffect
        state.animateCameraPosition(position.toCameraPosition())
    }

    LaunchedEffect(state) {
        state.events.collect { if (it is MapEvent.Idle) mapLoaded.value = true }
    }

    MaplibreMap(
        modifier = Modifier.fillMaxSize(),
        state = state,
        interactions = MapInteractions {
            callbacks {
                click {
                    onEvent { _ ->
                        clearOutageSelection()
                        ClickResult.Pass
                    }
                }
            }
        },
    )
}

@Composable
private fun RasterMapLayer(mapConfig: MapConfig.Raster) {
    val tiles = rememberRasterTileSource(
        tiles = mapConfig.tiles,
        tileSize = mapConfig.tileSize,
        options = TileSetOptions(
            minZoom = mapConfig.minZoom,
            maxZoom = mapConfig.maxZoom,
            attributionHtml = mapConfig.attributionHtml
        )
    )

    RasterLayer(id = "raster-maps", source = tiles)
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
    onOutageClicked: (Int) -> Unit
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