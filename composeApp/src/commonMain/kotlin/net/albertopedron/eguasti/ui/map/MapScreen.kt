package net.albertopedron.eguasti.ui.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Surface
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import eguasti.composeapp.generated.resources.Res
import eguasti.composeapp.generated.resources.app_name
import eguasti.composeapp.generated.resources.zilla_slab_bold
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.MaterialTheme
import eguasti.composeapp.generated.resources.alerts_title
import eguasti.composeapp.generated.resources.more_options
import eguasti.composeapp.generated.resources.notification_permission_denied_text
import eguasti.composeapp.generated.resources.notification_permission_denied_title
import eguasti.composeapp.generated.resources.ok
import eguasti.composeapp.generated.resources.settings_title
import net.albertopedron.eguasti.data.MapConfig
import net.albertopedron.eguasti.data.MapProviders
import net.albertopedron.eguasti.data.OutageTracker
import net.albertopedron.eguasti.data.model.AppMapState
import net.albertopedron.eguasti.data.model.Cause
import net.albertopedron.eguasti.data.model.Outage
import net.albertopedron.eguasti.ui.components.bottomSlideInVertically
import net.albertopedron.eguasti.ui.components.bottomSlideOutVertically
import net.albertopedron.eguasti.ui.map.maplibre.MapLibreMap
import net.albertopedron.eguasti.ui.theme.EGuastiTheme
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

private const val SEARCH_RESULT_ZOOM = 15.0

@Composable
fun MapScreen(
    targetLatitude: Double? = null,
    targetLongitude: Double? = null,
    onTargetHandled: () -> Unit = {},
    viewModel: MapViewModel = viewModel { MapViewModel() },
    navigateToSettings: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAlerts: () -> Unit,
) {
    val mapProvider by viewModel.mapProvider.collectAsState(null)
    val mapConfig by viewModel.mapConfig.collectAsState(null)
    val outages by viewModel.outages.collectAsState(emptyList())
    val selectedOutage by viewModel.selectedOutage.collectAsState()
    val tracking by viewModel.tracking.collectAsState()
    val followedCount = remember(tracking) { OutageTracker.getTracked().size }
    val mapState by viewModel.mapState.collectAsState(null)
    val trackOutagesEnabled by viewModel.trackOutagesEnabled.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val showPermissionDenied by viewModel.showPermissionDenied.collectAsState()

    LaunchedEffect(targetLatitude, targetLongitude) {
        if (targetLatitude != null && targetLongitude != null) {
            viewModel.centerOn(targetLatitude, targetLongitude, SEARCH_RESULT_ZOOM)
            onTargetHandled()
        }
    }

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
        navigateToSearch = navigateToSearch,
        navigateToAlerts = navigateToAlerts,
        snackbarHostState = snackbarHostState,
        mapProvider = mapProvider,
        mapConfig = mapConfig,
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
        followedCount = followedCount,
    )
}

@Composable
private fun MapScreen(
    snackbarHostState: SnackbarHostState,
    mapProvider: MapProviders?,
    mapConfig: MapConfig?,
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
    followedCount: Int,
    navigateToSettings: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAlerts: () -> Unit,
) {
    MapScreen(
        mapContent = { contentPadding ->
            AppMap(
                mapProvider = mapProvider,
                mapConfig = mapConfig,
                mapState = mapState,
                saveMapPosition = saveMapPosition,
                outages = outages,
                onOutageClicked = onOutageClicked,
                clearOutageSelection = clearOutageSelection,
                contentPadding = contentPadding,
            )
        },
        snackbarHostState = snackbarHostState,
        selectedOutage = selectedOutage,
        toggleTrackOutage = toggleTrackOutage,
        sheetVisible = sheetVisible,
        trackOutagesEnabled = trackOutagesEnabled,
        tracking = tracking,
        followedCount = followedCount,
        navigateToSettings = navigateToSettings,
        navigateToSearch = navigateToSearch,
        navigateToAlerts = navigateToAlerts,
    )
}

@Composable
private fun MapScreen(
    snackbarHostState: SnackbarHostState,
    mapContent: @Composable (PaddingValues) -> Unit = {},
    selectedOutage: Outage?,
    toggleTrackOutage: () -> Unit,
    sheetVisible: Boolean,
    trackOutagesEnabled: Boolean,
    tracking: Boolean,
    followedCount: Int,
    navigateToSettings: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAlerts: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            MapToolbar(navigateToSettings, navigateToSearch, navigateToAlerts, followedCount, trackOutagesEnabled)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        mapContent(innerPadding)
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier.fillMaxSize()
        ) {

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

@Composable
private fun MapToolbar(
    navigateToSettings: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAlerts: () -> Unit,
    followedCount: Int,
    trackOutagesEnabled: Boolean,
) {
    Row(
        modifier = Modifier.statusBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MapSearchToolbar(
            navigateToSettings = navigateToSettings,
            navigateToSearch = navigateToSearch,
            modifier = Modifier.weight(1f),
        )
        if (trackOutagesEnabled) {
            AlertsButton(onClick = navigateToAlerts, followedCount = followedCount)
        }
    }
}

@Composable
private fun LogoPlaceholder(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Filled.Bolt,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.size(2.dp))
        Text(
            stringResource(Res.string.app_name),
            fontFamily = FontFamily(Font(Res.font.zilla_slab_bold)),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun AlertsButton(onClick: () -> Unit, followedCount: Int) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        shadowElevation = 4.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            BadgedBox(badge = { if (followedCount > 0) Badge { Text(followedCount.toString()) } }) {
                Icon(Icons.Default.NotificationsNone, stringResource(Res.string.alerts_title))
            }
        }
    }
}

@Composable
private fun MapSearchToolbar(
    navigateToSettings: () -> Unit,
    navigateToSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Surface(
        onClick = navigateToSearch,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Search, contentDescription = null)
            Spacer(Modifier.size(12.dp))
            LogoPlaceholder(modifier = Modifier.weight(1f))
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, stringResource(Res.string.more_options))
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.settings_title)) },
                        leadingIcon = { Icon(Icons.Default.Settings, null) },
                        onClick = { menuExpanded = false; navigateToSettings() },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppMap(
    mapProvider: MapProviders?,
    mapConfig: MapConfig?,
    mapState: AppMapState?,
    saveMapPosition: (AppMapState) -> Unit,
    outages: List<Outage>,
    onOutageClicked: (Int) -> Unit,
    clearOutageSelection: () -> Unit,
    contentPadding: PaddingValues,
) {
    if (mapConfig == null) return

    MapLibreMap(
        mapConfig = mapConfig,
        mapState = mapState,
        saveMapPosition = saveMapPosition,
        outages = outages,
        onOutageClicked = onOutageClicked,
        clearOutageSelection = clearOutageSelection,
        contentPadding = contentPadding,
    )
}

@Composable
@Preview
private fun Preview() {
    EGuastiTheme {
        MapScreen(
            navigateToSettings = {},
            navigateToSearch = {},
            navigateToAlerts = {},
            snackbarHostState = remember { SnackbarHostState() },
            mapContent = { _ ->
                Box(Modifier.fillMaxSize().background(Color.LightGray))
            },
            selectedOutage = Outage(
                id = 0,
                start = "aaaaaa",
                expectedRestore = "Domani",
                lastUpdate = "aaaaa",
                place = "PADOVA",
                province = "Padova",
                latitude = 0.0,
                longitude = 0.9,
                offlineCustomers = 2,
                cause = Cause.FAILURE
            ),
            toggleTrackOutage = { },
            sheetVisible = true,
            trackOutagesEnabled = false,
            tracking = false,
            followedCount = 3,
        )
    }

}
