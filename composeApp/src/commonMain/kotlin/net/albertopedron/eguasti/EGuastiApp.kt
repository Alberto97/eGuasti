package net.albertopedron.eguasti

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.update
import net.albertopedron.eguasti.tools.DeviceConfigurationChanges
import net.albertopedron.eguasti.ui.map.MapScreen
//import net.albertopedron.eguasti.ui.search.SearchScreen
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
    const val MAP_ROUTE = "map"
    const val SEARCH_ROUTE = "search"
    const val SETTINGS_ROUTE = "settings"
}

@Composable
private fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Destinations.MAP_ROUTE) {
        composable(Destinations.MAP_ROUTE) {
            MapScreen(
                navigateToSettings = { navController.navigate(Destinations.SETTINGS_ROUTE) },
                navigateToSearch = {
                    navController.navigate(Destinations.SEARCH_ROUTE) {
                        popUpTo(Destinations.MAP_ROUTE)
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(Destinations.SEARCH_ROUTE) {
//            SearchScreen(
//                onNavigateToMap = {
//                    navController.navigate(Destinations.MAP_ROUTE) {
//                        popUpTo(Destinations.MAP_ROUTE) { inclusive = true }
//                    }
//                },
//            )
        }
        composable(Destinations.SETTINGS_ROUTE) {
            SettingsScreen(navigateBack =  { navController.popBackStack() })
        }
    }
}

@Composable
private fun DarkThemeChangeListener(darkTheme: Boolean = isSystemInDarkTheme()) {
    LaunchedEffect(darkTheme) {
        DeviceConfigurationChanges.darkTheme.update { darkTheme }
    }
}

