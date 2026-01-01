package net.albertopedron.eguasti.ui.map

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eguasti.composeapp.generated.resources.Res
import eguasti.composeapp.generated.resources.outage_blackout
import eguasti.composeapp.generated.resources.outage_cause
import eguasti.composeapp.generated.resources.outage_expected_restore
import eguasti.composeapp.generated.resources.outage_hide_details
import eguasti.composeapp.generated.resources.outage_last_update
import eguasti.composeapp.generated.resources.outage_maintenance
import eguasti.composeapp.generated.resources.outage_offline_customers
import eguasti.composeapp.generated.resources.outage_show_details
import eguasti.composeapp.generated.resources.outage_start_outage
import eguasti.composeapp.generated.resources.outage_start_tracking
import eguasti.composeapp.generated.resources.outage_stop_tracking
import net.albertopedron.eguasti.data.model.Cause
import net.albertopedron.eguasti.data.model.Outage
import net.albertopedron.eguasti.ui.theme.EGuastiTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OutageSheet(
    outage: Outage,
    tracking: Boolean,
    trackOutagesEnabled: Boolean,
    track: () -> Unit,
) {
    var showDetails by remember { mutableStateOf(false) }

    OutageSheet(
        showDetails = showDetails,
        onShowDetailsChange = { showDetails = it },
        outage = outage,
        tracking = tracking,
        trackOutagesEnabled = trackOutagesEnabled,
        track = track,
    )
}

@Composable
private fun OutageSheet(
    showDetails: Boolean,
    onShowDetailsChange: (Boolean) -> Unit,
    outage: Outage,
    tracking: Boolean,
    trackOutagesEnabled: Boolean,
    track: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.padding(end = 16.dp, start = 16.dp, bottom = 25.dp)) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 34.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 15.dp)) {
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        text = outage.place,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                    )

                    BuildType(outage = outage)

                    BuildItem(
                        icon = Icons.Default.Restore,
                        title = stringResource(Res.string.outage_expected_restore),
                        summary = outage.expectedRestore
                    )

                    AnimatedContent(
                        targetState = showDetails,
                    ) { show ->
                        if (show) {
                            Column {
                                BuildItems(outage = outage)
                                if (trackOutagesEnabled) {
                                    BuildTrackingButton(tracking, onClick = track)
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(0.dp))
                        }
                    }

                    BuildDetailsButton(
                        showDetails = showDetails,
                        onClick = { onShowDetailsChange(!showDetails) },
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

    }
}

@Composable
private fun BuildType(outage: Outage) {
    val (icon, reason) = if (outage.cause == Cause.FAILURE) {
        Icons.Default.Bolt to stringResource(Res.string.outage_blackout)
    } else {
        Icons.Default.Build to stringResource(Res.string.outage_maintenance)
    }
    BuildItem(icon = icon, title = stringResource(Res.string.outage_cause), summary = reason)
}

@Composable
private fun BuildItems(outage: Outage) {
    Column {
        BuildItem(
            icon = Icons.Default.ErrorOutline,
            title = stringResource(Res.string.outage_start_outage),
            summary = outage.start
        )
        BuildItem(
            icon = Icons.Default.SupervisedUserCircle,
            title = stringResource(Res.string.outage_offline_customers),
            summary = outage.offlineCustomers.toString()
        )
        BuildItem(
            icon = Icons.Default.AccessTime,
            title = stringResource(Res.string.outage_last_update),
            summary = outage.lastUpdate
        )
    }
}

@Composable
private fun BuildDetailsButton(showDetails: Boolean, onClick: () -> Unit) {
    val text = if (showDetails)
        stringResource(Res.string.outage_hide_details)
    else
        stringResource(Res.string.outage_show_details)

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun BuildTrackingButton(tracking: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
    ) {
//      Icon(
//          if (tracking) Icons.Default.NotificationsOff else Icons.Default.Notifications,
//          contentDescription = "",
//          modifier = Modifier.size(16.dp)
//      )
//      Spacer(Modifier.width(4.dp))
        Text(text = if (tracking) stringResource(Res.string.outage_stop_tracking) else stringResource(Res.string.outage_start_tracking) )
    }
}


@Composable
private fun BuildItem(icon: ImageVector, title: String, summary: String) {
    ListItem(
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(30.dp)
            )
        },
        headlineContent = { Text(title) },
        supportingContent = { Text(summary) }
    )
}

@Preview
@Composable
private fun Preview() {
    EGuastiTheme {
        OutageSheet(
            showDetails = true,
            onShowDetailsChange = {},
            outage = Outage(
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
            tracking = false,
            trackOutagesEnabled = true, // Added for preview
            track = {}
        )
    }
}

@Preview
@Composable
private fun PreviewTrackOutagesDisabled() {
    EGuastiTheme {
        OutageSheet(
            showDetails = true,
            onShowDetailsChange = {},
            outage = Outage(
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
            tracking = false,
            trackOutagesEnabled = false, // Added for preview
            track = {}
        )
    }
}
