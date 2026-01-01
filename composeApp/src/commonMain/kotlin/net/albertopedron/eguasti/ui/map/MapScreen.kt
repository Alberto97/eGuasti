package net.albertopedron.eguasti.ui.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.viewmodel.compose.viewModel
import eguasti.composeapp.generated.resources.Res
import eguasti.composeapp.generated.resources.app_name
import eguasti.composeapp.generated.resources.notification_permission_denied_text
import eguasti.composeapp.generated.resources.notification_permission_denied_title
import eguasti.composeapp.generated.resources.ok
import eguasti.composeapp.generated.resources.settings_title
import eguasti.composeapp.generated.resources.zilla_slab_bold
import net.albertopedron.eguasti.data.MapProviders
import net.albertopedron.eguasti.data.model.AppMapState
import net.albertopedron.eguasti.data.model.Cause
import net.albertopedron.eguasti.data.model.Outage
import net.albertopedron.eguasti.ui.components.AppToolbar
import net.albertopedron.eguasti.ui.components.bottomSlideInVertically
import net.albertopedron.eguasti.ui.components.bottomSlideOutVertically
import net.albertopedron.eguasti.ui.map.maplibre.MapLibreMap
import net.albertopedron.eguasti.ui.theme.EGuastiTheme
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel { MapViewModel() },
    navigateToSettings: () -> Unit,
) {
    val mapProvider by viewModel.mapProvider.collectAsState(null)
    val mapStyleUri by viewModel.mapStyleUri.collectAsState("")
    val outages by viewModel.outages.collectAsState(emptyList())
    val selectedOutage by viewModel.selectedOutage.collectAsState()
    val tracking by viewModel.tracking.collectAsState()
    val mapState by viewModel.mapState.collectAsState(null)
    val trackOutagesEnabled by viewModel.trackOutagesEnabled.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val showPermissionDenied by viewModel.showPermissionDenied.collectAsState()

    var sheetVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        val message = snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message = message, actionLabel = getString(Res.string.ok))
    }

    if (showPermissionDenied) {
        PermissionDeniedDialog(
            onDismiss = { viewModel.dismissPermissionDenied() },
        )
    }

    MapScreen(
        saveMapPosition = { state -> viewModel.saveMapPosition(state) },
        navigateToSettings = navigateToSettings,
        snackbarHostState = snackbarHostState,
        mapProvider = mapProvider,
        mapStyleUri = mapStyleUri,
        mapState = mapState,
        outages = outages,
        selectedOutage = selectedOutage,
        onOutageClicked = { id ->
            viewModel.selectOutage(id)
            sheetVisible = true
        },
        clearOutageSelection = {
            sheetVisible = false
        },
        toggleTrackOutage = {
            viewModel.toggleTrackOutage()
        },
        sheetVisible = sheetVisible,
        trackOutagesEnabled = trackOutagesEnabled,
        tracking = tracking,
    )
}

@Composable
private fun MapScreen(
    snackbarHostState: SnackbarHostState,
    mapProvider: MapProviders?,
    mapStyleUri: String,
    mapState: AppMapState?,
    saveMapPosition: (AppMapState) -> Unit,
    outages: List<Outage>,
    onOutageClicked: (Int) -> Unit,
    clearOutageSelection: () -> Unit,
    selectedOutage: Outage?,
    toggleTrackOutage: () -> Unit,
    sheetVisible: Boolean,
    trackOutagesEnabled: Boolean,
    tracking: Boolean,
    navigateToSettings: () -> Unit,
) {
    MapScreen(
        mapContent = {
            AppMap(
                mapProvider = mapProvider,
                styleUri = mapStyleUri,
                mapState = mapState,
                saveMapPosition = saveMapPosition,
                outages = outages,
                onOutageClicked = onOutageClicked,
                clearOutageSelection = clearOutageSelection,
            )
        },
        snackbarHostState = snackbarHostState,
        selectedOutage = selectedOutage,
        toggleTrackOutage = toggleTrackOutage,
        sheetVisible = sheetVisible,
        trackOutagesEnabled = trackOutagesEnabled,
        tracking = tracking,
        navigateToSettings = navigateToSettings,
    )
}

@Composable
private fun MapScreen(
    snackbarHostState: SnackbarHostState,
    mapContent: @Composable () -> Unit = {},
    selectedOutage: Outage?,
    toggleTrackOutage: () -> Unit,
    sheetVisible: Boolean,
    trackOutagesEnabled: Boolean,
    tracking: Boolean,
    navigateToSettings: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            MapToolbar(navigateToSettings = navigateToSettings)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier.padding(innerPadding)
        ) {
            mapContent()

            AnimatedVisibility(
                visible = sheetVisible && selectedOutage != null,
                enter = bottomSlideInVertically(),
                exit = bottomSlideOutVertically()
            ) {
                OutageSheet(
                    outage = selectedOutage!!,
                    trackOutagesEnabled = trackOutagesEnabled,
                    tracking = tracking,
                    track = toggleTrackOutage
                )
            }
        }
    }
}

@Composable
private fun PermissionDeniedDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(resource = Res.string.notification_permission_denied_title)) },
        text = { Text(text = stringResource(resource = Res.string.notification_permission_denied_text)) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(Res.string.ok))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapToolbar(navigateToSettings: () -> Unit) {
    AppToolbar(
        title = {
            Text(
                stringResource(Res.string.app_name),
                fontFamily = FontFamily(Font(Res.font.zilla_slab_bold)),
            )
        },
        actions = {
            IconButton(onClick = navigateToSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(Res.string.settings_title),
                )
            }
        }
    )
}

@Composable
private fun AppMap(
    mapProvider: MapProviders?,
    styleUri: String,
    mapState: AppMapState?,
    saveMapPosition: (AppMapState) -> Unit,
    outages: List<Outage>,
    onOutageClicked: (Int) -> Unit,
    clearOutageSelection: () -> Unit,
) {
//    if (mapProvider == MapProviders.MapBox) {
//        MapboxMap(
//            styleUri = styleUri,
//            mapState = mapState,
//            saveMapPosition = saveMapPosition,
//            outages = outages,
//            onOutageClicked = onOutageClicked,
//            clearOutageSelection = clearOutageSelection,
//        )
//    } else {
        MapLibreMap(
            styleUri = styleUri,
            mapState = mapState,
            saveMapPosition = saveMapPosition,
            outages = outages,
            onOutageClicked = onOutageClicked,
            clearOutageSelection = clearOutageSelection,
        )
//    }
}

@Composable
@Preview
private fun Preview() {
    EGuastiTheme {
        MapScreen(
            navigateToSettings = {},
            snackbarHostState = remember { SnackbarHostState() },
            mapContent = {
                Box(Modifier.fillMaxSize().background(Color.LightGray))
            },
            selectedOutage = Outage(
                id = 0,
                start = "aaaaaa",
                expectedRestore = "Domani",
                lastUpdate = "aaaaa",
                place = "PADOVA",
                latitude = 0.0,
                longitude = 0.9,
                offlineCustomers = 2,
                cause = Cause.FAILURE
            ),
            toggleTrackOutage = { },
            sheetVisible = true,
            trackOutagesEnabled = false,
            tracking = false,
        )
    }

}
