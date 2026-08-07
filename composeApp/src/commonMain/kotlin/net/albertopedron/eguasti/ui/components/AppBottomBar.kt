package net.albertopedron.eguasti.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import eguasti.composeapp.generated.resources.Res
import eguasti.composeapp.generated.resources.nav_alerts
import eguasti.composeapp.generated.resources.nav_map
import eguasti.composeapp.generated.resources.nav_search
import org.jetbrains.compose.resources.stringResource

enum class BottomBarTab { Map, Search, Alerts }

@Composable
fun AppBottomBar(
    selected: BottomBarTab,
    onTabSelected: (BottomBarTab) -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = selected == BottomBarTab.Map,
            onClick = { onTabSelected(BottomBarTab.Map) },
            icon = { Icon(imageVector = Icons.Filled.Map, contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_map)) },
        )
        NavigationBarItem(
            selected = selected == BottomBarTab.Search,
            onClick = { onTabSelected(BottomBarTab.Search) },
            icon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_search)) },
        )
        NavigationBarItem(
            selected = selected == BottomBarTab.Alerts,
            onClick = { onTabSelected(BottomBarTab.Alerts) },
            icon = { Icon(imageVector = Icons.Filled.Notifications, contentDescription = null) },
            label = { Text(stringResource(Res.string.nav_alerts)) },
        )
    }
}
