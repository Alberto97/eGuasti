package net.albertopedron.eguasti

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import kotlinx.coroutines.flow.update
import net.albertopedron.eguasti.tools.DeviceConfigurationChanges
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
    private const val MAP_PATH = "map"
    private const val MAP_LAT_ARG = "lat"
    private const val MAP_LNG_ARG = "lng"
    const val MAP_ROUTE = "$MAP_PATH?$MAP_LAT_ARG={$MAP_LAT_ARG}&$MAP_LNG_ARG={$MAP_LNG_ARG}"
    const val SEARCH_ROUTE = "search"
    const val SETTINGS_ROUTE = "settings"

    const val MAP_LAT_KEY = MAP_LAT_ARG
    const val MAP_LNG_KEY = MAP_LNG_ARG

    fun mapRoute(): String = MAP_PATH
    fun mapRoute(latitude: Double, longitude: Double): String =
        "$MAP_PATH?$MAP_LAT_ARG=$latitude&$MAP_LNG_ARG=$longitude"
}

@Composable
private fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Destinations.mapRoute()) {
        composable(
            route = Destinations.MAP_ROUTE,
            arguments = listOf(
                navArgument(Destinations.MAP_LAT_KEY) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(Destinations.MAP_LNG_KEY) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) { backStackEntry ->
            val lat = backStackEntry.arguments
                ?.read { getStringOrNull(Destinations.MAP_LAT_KEY) }
                ?.toDoubleOrNull()
            val lng = backStackEntry.arguments
                ?.read { getStringOrNull(Destinations.MAP_LNG_KEY) }
                ?.toDoubleOrNull()
            MapScreen(
                targetLatitude = lat,
                targetLongitude = lng,
                navigateToSettings = { navController.navigate(Destinations.SETTINGS_ROUTE) },
                navigateToSearch = { navController.navigate(Destinations.SEARCH_ROUTE) },
            )
        }
        composable(Destinations.SEARCH_ROUTE) {
            SearchScreen(
                onNavigateToMap = {
                    navController.navigate(Destinations.mapRoute()) {
                        popUpTo(Destinations.MAP_ROUTE) { inclusive = true }
                    }
                },
                onNavigateToMapAt = { latitude, longitude ->
                    navController.navigate(Destinations.mapRoute(latitude, longitude)) {
                        popUpTo(Destinations.MAP_ROUTE) { inclusive = true }
                    }
                },
            )
        }
        composable(Destinations.SETTINGS_ROUTE) {
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
