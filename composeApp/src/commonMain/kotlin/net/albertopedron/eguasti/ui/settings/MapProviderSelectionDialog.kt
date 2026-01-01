package net.albertopedron.eguasti.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import eguasti.composeapp.generated.resources.Res
import eguasti.composeapp.generated.resources.cancel
import eguasti.composeapp.generated.resources.settings_map_provider_selection_title
import kotlin.collections.forEach
import net.albertopedron.eguasti.data.MapProvider
import org.jetbrains.compose.resources.stringResource

@Composable
fun MapProviderSelectionDialog(
    availableProviders: List<MapProvider>,
    currentProvider: MapProvider?,
    onProviderSelected: (MapProvider) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.settings_map_provider_selection_title)) },
        text = {
            Column {
                availableProviders.forEach { provider ->
                    ListItem(
                        headlineContent = {
                            Text(
                                text = provider.getValue().name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        leadingContent = {
                            RadioButton(
                                selected = (provider.getValue() == currentProvider?.getValue()),
                                onClick = null // Recommended when the whole item is selectable
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (provider.getValue() == currentProvider?.getValue()),
                                onClick = { onProviderSelected(provider) }
                            ),
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}
