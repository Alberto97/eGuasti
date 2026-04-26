package net.albertopedron.eguasti.ui.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eguasti.composeapp.generated.resources.Res
import eguasti.composeapp.generated.resources.outage_blackout
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CauseChip(cause = outage.cause)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = outage.place.uppercase(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )

            if (outage.province.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                ProvinceSubtitle(province = outage.province)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                DetailRow(
                    icon = Icons.Filled.History,
                    label = stringResource(Res.string.outage_expected_restore).uppercase(),
                    value = outage.expectedRestore,
                )

                AnimatedVisibility(visible = showDetails) {
                    Column {
                        Spacer(modifier = Modifier.height(14.dp))
                        DetailRow(
                            icon = Icons.Filled.EventBusy,
                            label = stringResource(Res.string.outage_start_outage).uppercase(),
                            value = outage.start,
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        DetailRow(
                            icon = Icons.Filled.People,
                            label = stringResource(Res.string.outage_offline_customers).uppercase(),
                            value = outage.offlineCustomers.toString(),
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        DetailRow(
                            icon = Icons.Filled.Update,
                            label = stringResource(Res.string.outage_last_update).uppercase(),
                            value = outage.lastUpdate,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(visible = showDetails && trackOutagesEnabled) {
                Column {
                    NotifyButton(
                        tracking = tracking,
                        onClick = track,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            ShowDetailsButton(
                showDetails = showDetails,
                onClick = { onShowDetailsChange(!showDetails) },
            )
        }
    }
}

@Composable
private fun CauseChip(cause: Cause) {
    val (bgColor, dotColor, label) = when (cause) {
        Cause.FAILURE -> Triple(
            Color(0xFFC53030).copy(alpha = 0.2f),
            Color(0xFFC53030),
            stringResource(Res.string.outage_blackout),
        )
        Cause.MAINTENANCE -> Triple(
            //Color(0xFFF8E5A0)
            Color(0xFFA88300).copy(alpha = 0.2f),
            Color(0xFFA88300),
            stringResource(Res.string.outage_maintenance),
        )
    }

    Surface(
        shape = RoundedCornerShape(percent = 50),
        color = bgColor,
        modifier = Modifier.wrapContentSize(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color = dotColor, shape = CircleShape),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
            )
        }
    }
}

@Composable
private fun ProvinceSubtitle(province: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.wrapContentSize(),
    ) {
        Icon(
            imageVector = Icons.Filled.Apartment,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = province,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun NotifyButton(
    tracking: Boolean,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(percent = 50),
    ) {
        Icon(
            imageVector = if (tracking) Icons.Filled.NotificationsOff else Icons.Filled.Notifications,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (tracking) {
                stringResource(Res.string.outage_stop_tracking)
            } else {
                stringResource(Res.string.outage_start_tracking)
            },
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun ShowDetailsButton(
    showDetails: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(percent = 50),
    ) {
        Text(
            text = if (showDetails)
                stringResource(Res.string.outage_hide_details)
            else
                stringResource(Res.string.outage_show_details),
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Preview
@Composable
private fun PreviewMaintenance() {
    EGuastiTheme {
        OutageSheet(
            showDetails = false,
            onShowDetailsChange = {},
            outage = Outage(
                id = 0,
                start = "30/03/2026 08:50",
                expectedRestore = "In corso di definizione",
                lastUpdate = "30/03/2026 21:31",
                place = "Comacchio Sud",
                province = "Ferrara",
                latitude = 0.0,
                longitude = 0.9,
                offlineCustomers = 20,
                cause = Cause.MAINTENANCE,
            ),
            tracking = false,
            trackOutagesEnabled = true,
            track = {},
        )
    }
}

@Preview
@Composable
private fun PreviewFailureExpanded() {
    EGuastiTheme {
        OutageSheet(
            showDetails = true,
            onShowDetailsChange = {},
            outage = Outage(
                id = 0,
                start = "30/03/2026 08:50",
                expectedRestore = "In corso di definizione",
                lastUpdate = "30/03/2026 21:31",
                place = "Comacchio Sud",
                province = "Ferrara",
                latitude = 0.0,
                longitude = 0.9,
                offlineCustomers = 20,
                cause = Cause.FAILURE,
            ),
            tracking = false,
            trackOutagesEnabled = true,
            track = {},
        )
    }
}

@Preview
@Composable
private fun PreviewExpandedTrackingDisabled() {
    EGuastiTheme {
        OutageSheet(
            showDetails = true,
            onShowDetailsChange = {},
            outage = Outage(
                id = 0,
                start = "30/03/2026 08:50",
                expectedRestore = "In corso di definizione",
                lastUpdate = "30/03/2026 21:31",
                place = "Comacchio Sud",
                province = "Ferrara",
                latitude = 0.0,
                longitude = 0.9,
                offlineCustomers = 20,
                cause = Cause.MAINTENANCE,
            ),
            tracking = false,
            trackOutagesEnabled = false,
            track = {},
        )
    }
}
