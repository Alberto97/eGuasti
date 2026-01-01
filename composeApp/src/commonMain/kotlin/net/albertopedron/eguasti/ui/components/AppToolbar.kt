package net.albertopedron.eguasti.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import eguasti.composeapp.generated.resources.Res
import eguasti.composeapp.generated.resources.app_name
import net.albertopedron.eguasti.ui.theme.EGuastiTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppToolbar(
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable () -> Unit = {}
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.primary
        ),
        title = title,
        actions = actions,
        navigationIcon = navigationIcon,
    )
}

@Preview
@Composable
private fun Preview() {
    EGuastiTheme {
        AppToolbar(title = { Text(stringResource(Res.string.app_name)) })
    }
}