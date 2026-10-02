package net.albertopedron.eguasti

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.toRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.update
import net.albertopedron.eguasti.tools.DeviceConfigurationChanges
import net.albertopedron.eguasti.ui.alerts.AlertsScreen
import net.albertopedron.eguasti.ui.map.MapScreen
import net.albertopedron.eguasti.ui.search.SearchScreen
import net.albertopedron.eguasti.ui.settings.SettingsScreen
import net.albertopedron.eguasti.ui.theme.EGuastiTheme


@Composable
fun EGuastiApp() {
    EGuastiTheme {
        NavGraph()
        DarkThemeChangeListener()
    }
}

object Destinations {
    const val MAP_TARGET_KEY = "mapTarget"

    @Serializable
    data class Map(val latitude: Double? = null, val longitude: Double? = null)

    @Serializable
    data object Search

    @Serializable
    data object Alerts

    @Serializable
    data object Settings
}

private fun NavController.returnToMap(latitude: Double, longitude: Double) {
    previousBackStackEntry?.savedStateHandle?.set(
        Destinations.MAP_TARGET_KEY,
        Json.encodeToString(Destinations.Map(latitude, longitude)),
    )
    popBackStack()
}

@Composable
private fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Destinations.Map()) {
        composable<Destinations.Map> { backStackEntry ->
            val destination = backStackEntry.toRoute<Destinations.Map>()
            val mapTarget by backStackEntry.savedStateHandle
                .getStateFlow<String?>(Destinations.MAP_TARGET_KEY, null).collectAsState()
            val target = mapTarget?.let { Json.decodeFromString<Destinations.Map>(it) } ?: destination
            MapScreen(
                targetLatitude = target.latitude,
                targetLongitude = target.longitude,
                onTargetHandled = { backStackEntry.savedStateHandle.set<String?>(Destinations.MAP_TARGET_KEY, null) },
                navigateToSettings = { navController.navigate(Destinations.Settings) },
                navigateToSearch = { navController.navigate(Destinations.Search) },
                navigateToAlerts = { navController.navigate(Destinations.Alerts) },
            )
        }
        composable<Destinations.Search> {
            SearchScreen(
                onNavigateToMap = {
                    navController.popBackStack()
                },
                onNavigateToMapAt = { latitude, longitude ->
                    navController.returnToMap(latitude, longitude)
                },
            )
        }
        composable<Destinations.Alerts> {
            AlertsScreen(
                onNavigateToMap = {
                    navController.popBackStack()
                },
                onNavigateToMapAt = { latitude, longitude ->
                    navController.returnToMap(latitude, longitude)
                },
            )
        }
        composable<Destinations.Settings> {
            SettingsScreen(navigateBack = { navController.popBackStack() })
        }
    }
}

@Composable
private fun DarkThemeChangeListener(darkTheme: Boolean = isSystemInDarkTheme()) {
    LaunchedEffect(darkTheme) {
        DeviceConfigurationChanges.darkTheme.update { darkTheme }
    }
}
