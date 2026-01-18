package net.albertopedron.eguasti.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Http
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import eguasti.composeapp.generated.resources.Res
import eguasti.composeapp.generated.resources.about_category_info
import eguasti.composeapp.generated.resources.about_last_db_update
import eguasti.composeapp.generated.resources.about_leave_feedback_title
import eguasti.composeapp.generated.resources.about_repository_title
import eguasti.composeapp.generated.resources.about_version
import eguasti.composeapp.generated.resources.ic_github
import eguasti.composeapp.generated.resources.ok
import eguasti.composeapp.generated.resources.settings_experimental_section_title
import eguasti.composeapp.generated.resources.settings_experimental_title
import eguasti.composeapp.generated.resources.settings_fetch_pbf_subtitle
import eguasti.composeapp.generated.resources.settings_fetch_pbf_title
import eguasti.composeapp.generated.resources.settings_map_provider_title
import eguasti.composeapp.generated.resources.settings_title
import eguasti.composeapp.generated.resources.settings_track_outages_dialog_message
import eguasti.composeapp.generated.resources.settings_track_outages_dialog_title
import eguasti.composeapp.generated.resources.settings_track_outages_subtitle
import eguasti.composeapp.generated.resources.settings_track_outages_title
import net.albertopedron.eguasti.ui.theme.EGuastiTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel { SettingsViewModel() },
    navigateBack: () -> Unit
) {
    val appVersion = viewModel.appVersion
    val lastDbUpdate: String by viewModel.dbVersion.collectAsState("")
    val currentMapProvider by viewModel.currentMapProvider.collectAsState()
    val showMapProviderDialog by viewModel.showMapProviderDialog.collectAsState()
    val experimentalSettingsEnabled by viewModel.experimentalSettingsEnabled.collectAsState()
    val trackOutagesEnabled by viewModel.trackOutagesEnabled.collectAsState()
    val fetchUsingProtocolBuffers by viewModel.fetchUsingProtocolBuffers.collectAsState()
    var showTrackOutagesDialog by remember { mutableStateOf(false) }

    if (showMapProviderDialog) {
        MapProviderSelectionDialog(
            availableProviders = viewModel.availableMapProviders,
            currentProvider = currentMapProvider,
            onProviderSelected = { provider -> viewModel.setCurrentMapProvider(provider.getValue()) },
            onDismiss = { viewModel.closeMapProviderDialog() }
        )
    }

    if (showTrackOutagesDialog) {
        TrackOutagesInfoDialog(
            onDismiss = { showTrackOutagesDialog = false },
        )
    }

    SettingsScreen(
        appVersion = appVersion,
        lastDbUpdate = lastDbUpdate,
        openRepository = { viewModel.openRepository() },
        openAppStoreForReview = { viewModel.openAppStoreForReview() },
        onBackClick = { navigateBack() },
        currentMapProviderName = currentMapProvider?.getValue()?.name ?: "",
        onMapProviderClick = { viewModel.openMapProviderDialog() },
        experimentalSettingsEnabled = experimentalSettingsEnabled,
        onExperimentalSettingsToggle = { viewModel.setExperimentalSettingsEnabled(it) },
        trackOutagesEnabled = trackOutagesEnabled,
        onTrackOutagesToggle = { isEnabled ->
            viewModel.setTrackOutagesEnabled(isEnabled)
            if (isEnabled) {
                showTrackOutagesDialog = true
            }
        },
        fetchUsingProtocolBuffers = fetchUsingProtocolBuffers,
        onFetchUsingProtocolBuffersToggle = { viewModel.setFetchUsingProtocolBuffers(it) }
    )
}

@Composable
private fun TrackOutagesInfoDialog(
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.settings_track_outages_dialog_title)) },
        text = { Text(stringResource(Res.string.settings_track_outages_dialog_message)) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.ok))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    openRepository: () -> Unit,
    openAppStoreForReview: () -> Unit,
    appVersion: String,
    lastDbUpdate: String,
    currentMapProviderName: String,
    onMapProviderClick: () -> Unit,
    experimentalSettingsEnabled: Boolean,
    onExperimentalSettingsToggle: (Boolean) -> Unit,
    trackOutagesEnabled: Boolean,
    onTrackOutagesToggle: (Boolean) -> Unit,
    fetchUsingProtocolBuffers: Boolean,
    onFetchUsingProtocolBuffersToggle: (Boolean) -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(Res.string.settings_title)) },
            navigationIcon = {
                IconButton(onClick = { onBackClick() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            },
        )
    }) { contentPadding ->
        Column(Modifier.padding(contentPadding)) {
            ListItem(
                leadingContent = { ListIcon { Icon(painterResource(Res.drawable.ic_github), null) } },
                headlineContent = { Text(stringResource(Res.string.about_repository_title)) },
                modifier = Modifier.clickable { openRepository() }
            )
            ListItem(
                leadingContent = { ListIcon { Icon(Icons.Rounded.Favorite, null) } },
                headlineContent = { Text(stringResource(Res.string.about_leave_feedback_title)) },
                modifier = Modifier.clickable { openAppStoreForReview() }
            )

            SettingsSection(
                currentMapProviderName = currentMapProviderName,
                onMapProviderClick = onMapProviderClick,
                experimentalSettingsEnabled = experimentalSettingsEnabled,
                onExperimentalSettingsToggle = onExperimentalSettingsToggle
            )

            if (experimentalSettingsEnabled) {
                ExperimentalSettingsSection(
                    trackOutagesEnabled = trackOutagesEnabled,
                    onTrackOutagesToggle = onTrackOutagesToggle,
                    fetchUsingProtocolBuffers = fetchUsingProtocolBuffers,
                    onFetchUsingProtocolBuffersToggle = onFetchUsingProtocolBuffersToggle
                )
            }

            HorizontalDivider(color = Color.LightGray)
            ListItem(
                leadingContent = {},
                headlineContent = {
                    Text(
                        stringResource(Res.string.about_category_info),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            )
            ListItem(
                leadingContent = {},
                headlineContent = { Text(stringResource(Res.string.about_version)) },
                supportingContent = { Text(appVersion) }
            )
            ListItem(
                leadingContent = { },
                headlineContent = { Text(stringResource(Res.string.about_last_db_update)) },
                supportingContent = { Text(lastDbUpdate) },
            )
        }
    }
}

@Composable
private fun ListIcon(child: @Composable () -> Unit) {
    Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
        child()
    }
}

@Composable
private fun SettingsSection(
    currentMapProviderName: String,
    onMapProviderClick: () -> Unit,
    experimentalSettingsEnabled: Boolean,
    onExperimentalSettingsToggle: (Boolean) -> Unit
) {
    Column {
        HorizontalDivider(color = Color.LightGray)
        ListItem(
            leadingContent = {},
            headlineContent = {
                Text(
                    stringResource(Res.string.settings_title),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        )
        MapProviderSettingsItem(currentMapProviderName, onMapProviderClick)
        ExperimentalSettingsToggleItem(
            checked = experimentalSettingsEnabled,
            onCheckedChange = onExperimentalSettingsToggle
        )
    }
}

@Composable
private fun MapProviderSettingsItem(
    currentMapProviderName: String,
    onMapProviderClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable { onMapProviderClick() },
        leadingContent = { ListIcon { Icon(Icons.Rounded.Map, null) } },
        headlineContent = { Text(stringResource(Res.string.settings_map_provider_title)) },
        supportingContent = { Text(currentMapProviderName) }
    )
}

@Composable
private fun ExperimentalSettingsToggleItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        modifier = Modifier.clickable { onCheckedChange(!checked) },
        leadingContent = { ListIcon { Icon(Icons.Rounded.Science, contentDescription = null) } },
        headlineContent = { Text(stringResource(Res.string.settings_experimental_title)) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}

@Composable
private fun ExperimentalSettingsSection(
    trackOutagesEnabled: Boolean,
    onTrackOutagesToggle: (Boolean) -> Unit,
    fetchUsingProtocolBuffers: Boolean,
    onFetchUsingProtocolBuffersToggle: (Boolean) -> Unit
) {
    Column {
        HorizontalDivider(color = Color.LightGray)
        ListItem(
            leadingContent = {},
            headlineContent = {
                Text(
                    stringResource(Res.string.settings_experimental_section_title),
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        )
        ListItem(
            modifier = Modifier.clickable { onTrackOutagesToggle(!trackOutagesEnabled) },
            leadingContent = { ListIcon { Icon(Icons.Rounded.Notifications, contentDescription = null) } },
            headlineContent = { Text(stringResource(Res.string.settings_track_outages_title)) },
            supportingContent = { Text(stringResource(Res.string.settings_track_outages_subtitle)) },
            trailingContent = {
                Switch(
                    checked = trackOutagesEnabled,
                    onCheckedChange = onTrackOutagesToggle
                )
            }
        )
        ListItem(
            modifier = Modifier.clickable { onFetchUsingProtocolBuffersToggle(!fetchUsingProtocolBuffers) },
            leadingContent = { ListIcon { Icon(Icons.Rounded.Http, contentDescription = null) } },
            headlineContent = { Text(stringResource(Res.string.settings_fetch_pbf_title)) },
            supportingContent = { Text(stringResource(Res.string.settings_fetch_pbf_subtitle)) },
            trailingContent = {
                Switch(
                    checked = fetchUsingProtocolBuffers,
                    onCheckedChange = onFetchUsingProtocolBuffersToggle
                )
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    EGuastiTheme {
        Surface {
            SettingsScreen(
                onBackClick = { },
                openRepository = { },
                openAppStoreForReview = { },
                appVersion = "1.0",
                lastDbUpdate = "Today",
                currentMapProviderName = "MapBox",
                onMapProviderClick = {},
                experimentalSettingsEnabled = true,
                onExperimentalSettingsToggle = {},
                trackOutagesEnabled = true,
                onTrackOutagesToggle = {},
                fetchUsingProtocolBuffers = true,
                onFetchUsingProtocolBuffersToggle = {}
            )
        }
    }
}

@Preview
@Composable
private fun PreviewExperimentalDisabled() {
    EGuastiTheme {
        Surface {
            SettingsScreen(
                onBackClick = { },
                openRepository = { },
                openAppStoreForReview = { },
                appVersion = "1.0",
                lastDbUpdate = "Today",
                currentMapProviderName = "MapBox",
                onMapProviderClick = {},
                experimentalSettingsEnabled = false,
                onExperimentalSettingsToggle = {},
                trackOutagesEnabled = false,
                onTrackOutagesToggle = {},
                fetchUsingProtocolBuffers = true,
                onFetchUsingProtocolBuffersToggle = {}
            )
        }
    }
}
