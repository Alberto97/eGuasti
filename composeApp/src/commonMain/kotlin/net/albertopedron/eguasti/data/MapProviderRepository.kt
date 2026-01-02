package net.albertopedron.eguasti.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

enum class MapProviders {
    OpenStreetMap,
    MapTiler,
    OpenFreeMap
}

class MapProviderRepository(
    private val persistence: Persistence = Persistence,
) {
    private val allProviders = listOf(
        OpenStreetMapProvider(),
        MapTilerProvider(),
        OpenFreeMapProvider(),
    )

    fun getAvailable(): List<MapProvider> {
        return allProviders.filter { provider -> provider.isAvailable() }
    }

    fun getCurrentMapConfig(darkMode: Boolean): Flow<MapConfig> {
        return getCurrent().map { provider -> provider.getConfig(darkMode) }
    }

    fun getCurrent(): Flow<MapProvider> {
        return persistence.getCurrentMapProvider().map { storedProvider ->
            storedProvider?.let {
                allProviders.firstOrNull { provider -> provider.getValue().name == storedProvider }
            } ?: getAvailable().first()
        }
    }

    suspend fun setCurrent(value: MapProviders) = withContext(Dispatchers.IO) {
        persistence.setCurrentMapProvider(value.name)
    }
}