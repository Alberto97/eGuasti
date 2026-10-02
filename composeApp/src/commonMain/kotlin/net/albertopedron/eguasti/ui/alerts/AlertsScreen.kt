package net.albertopedron.eguasti.ui.alerts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Map
import eguasti.composeapp.generated.resources.navigate_back
import eguasti.composeapp.generated.resources.more_options
import eguasti.composeapp.generated.resources.alerts_empty
import eguasti.composeapp.generated.resources.alerts_show_map
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eguasti.composeapp.generated.resources.Res
import eguasti.composeapp.generated.resources.alerts_chip_failure
import eguasti.composeapp.generated.resources.alerts_chip_maintenance
import eguasti.composeapp.generated.resources.alerts_dismiss
import eguasti.composeapp.generated.resources.alerts_filter_all
import eguasti.composeapp.generated.resources.alerts_filter_failures
import eguasti.composeapp.generated.resources.alerts_filter_maintenances
import eguasti.composeapp.generated.resources.alerts_title
import eguasti.composeapp.generated.resources.outage_expected_restore
import net.albertopedron.eguasti.data.model.Cause
import net.albertopedron.eguasti.ui.theme.EGuastiTheme
import org.jetbrains.compose.resources.stringResource

data class Alert(
    val id: Int,
    val place: String,
    val expectedRestore: String,
    val cause: Cause,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)

enum class AlertFilter { All, Failures, Maintenances }

@Composable
fun AlertsScreen(
    viewModel: AlertsViewModel = viewModel { AlertsViewModel() },
    onNavigateToMap: () -> Unit = {},
    onNavigateToMapAt: (Double, Double) -> Unit = { _, _ -> },
) {
    val alerts by viewModel.alerts.collectAsState()

    AlertsScreen(
        alerts = alerts,
        onDismiss = viewModel::dismiss,
        onNavigateToMap = onNavigateToMap,
        onNavigateToMapAt = onNavigateToMapAt,
    )
}

@Composable
private fun AlertsScreen(
    alerts: List<Alert>,
    onDismiss: (Alert) -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToMapAt: (Double, Double) -> Unit,
) {
    var filter by rememberSaveable { mutableStateOf(AlertFilter.All) }

    val filteredAlerts = remember(filter, alerts) {
        when (filter) {
            AlertFilter.All -> alerts
            AlertFilter.Failures -> alerts.filter { it.cause == Cause.FAILURE }
            AlertFilter.Maintenances -> alerts.filter { it.cause == Cause.MAINTENANCE }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        topBar = { AlertsTopBar(onNavigateToMap) },
    ) { innerPadding ->
        AlertsContent(
            filter = filter,
            onFilterChange = { filter = it },
            alerts = filteredAlerts,
            onDismiss = onDismiss,
            contentPadding = innerPadding,
            onShowOnMap = { onNavigateToMapAt(it.latitude, it.longitude) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlertsTopBar(navigateBack: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(Res.string.alerts_title)) },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(Res.string.navigate_back))
            }
        },
    )
}

@Composable
private fun AlertsContent(
    filter: AlertFilter,
    onFilterChange: (AlertFilter) -> Unit,
    alerts: List<Alert>,
    onDismiss: (Alert) -> Unit,
    contentPadding: PaddingValues,
    onShowOnMap: (Alert) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
    ) {
        Spacer(Modifier.size(16.dp))
        FilterChipsRow(
            selected = filter,
            onFilterChange = onFilterChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.size(20.dp))

        if (alerts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    stringResource(Res.string.alerts_empty),
                    modifier = Modifier.padding(24.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 16.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(items = alerts, key = { it.id }) { alert ->
                    AlertCard(
                        alert = alert,
                        onDismiss = { onDismiss(alert) },
                        onShowOnMap = { onShowOnMap(alert) },
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChipsRow(
    selected: AlertFilter,
    onFilterChange: (AlertFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AlertsFilterChip(
            label = stringResource(Res.string.alerts_filter_all),
            isSelected = selected == AlertFilter.All,
            onClick = { onFilterChange(AlertFilter.All) },
        )
        AlertsFilterChip(
            label = stringResource(Res.string.alerts_filter_failures),
            isSelected = selected == AlertFilter.Failures,
            onClick = { onFilterChange(AlertFilter.Failures) },
        )
        AlertsFilterChip(
            label = stringResource(Res.string.alerts_filter_maintenances),
            isSelected = selected == AlertFilter.Maintenances,
            onClick = { onFilterChange(AlertFilter.Maintenances) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlertsFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontWeight = FontWeight.Medium,
            )
        },
        shape = RoundedCornerShape(percent = 50),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        ),
        border = null,
    )
}

@Composable
private fun AlertCard(
    alert: Alert,
    onDismiss: () -> Unit,
    onShowOnMap: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                AlertCauseChip(cause = alert.cause)

                Spacer(modifier = Modifier.weight(1f))

                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, stringResource(Res.string.more_options))
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.alerts_dismiss)) },
                            onClick = { menuExpanded = false; onDismiss() },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = alert.place,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.size(10.dp))

            Text(
                text = stringResource(Res.string.outage_expected_restore).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = alert.expectedRestore,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
            Spacer(modifier = Modifier.size(8.dp))
            TextButton(onClick = onShowOnMap, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Map, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text(stringResource(Res.string.alerts_show_map))
            }

        }
    }
}

@Composable
private fun AlertCauseChip(cause: Cause) {
    val (bgColor, contentColor, icon, label) = when (cause) {
        Cause.FAILURE -> AlertChipStyle(
            background = Color(0xFFD83A3A),
            content = Color.White,
            icon = Icons.Filled.Bolt,
            label = stringResource(Res.string.alerts_chip_failure),
        )
        Cause.MAINTENANCE -> AlertChipStyle(
            background = Color(0xFFF5C03C),
            content = Color(0xFF1F1F1F),
            icon = Icons.Filled.Engineering,
            label = stringResource(Res.string.alerts_chip_maintenance),
        )
    }

    Surface(
        shape = RoundedCornerShape(percent = 50),
        color = bgColor,
        modifier = Modifier.wrapContentSize(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = contentColor,
            )
        }
    }
}

private data class AlertChipStyle(
    val background: Color,
    val content: Color,
    val icon: ImageVector,
    val label: String,
)

private val sampleAlerts = listOf(
    Alert(
        id = 1,
        place = "Via Roma, Comacchio",
        expectedRestore = "Oggi alle 18:30",
        cause = Cause.FAILURE,
    ),
    Alert(
        id = 2,
        place = "Piazza del Duomo, Milano",
        expectedRestore = "Domani alle 09:00",
        cause = Cause.MAINTENANCE,
    ),
    Alert(
        id = 3,
        place = "Via Mazzini, Ferrara",
        expectedRestore = "15 Ott alle 21:00",
        cause = Cause.FAILURE,
    ),
    Alert(
        id = 4,
        place = "Corso Italia, Cortina",
        expectedRestore = "Oggi alle 14:15",
        cause = Cause.MAINTENANCE,
    ),
)

@Preview
@Composable
private fun AlertsScreenPreview() {
    EGuastiTheme {
        AlertsScreen(
            alerts = sampleAlerts,
            onDismiss = {},
            onNavigateToMap = {},
            onNavigateToMapAt = { _, _ -> },
        )
    }
}
