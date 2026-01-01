package net.albertopedron.eguasti.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

enum class MapProviders {
    MapTiler,
    OpenFreeMap
}

class MapProviderRepository(
    private val persistence: Persistence = Persistence,
) {
    private val allProviders = listOf(
        MapTilerProvider(),
        OpenFreeMapProvider(),
    )

    fun getAvailable(): List<MapProvider> {
        return allProviders.filter { provider -> provider.isAvailable() }
    }

    fun getCurrentMapsUri(darkMode: Boolean): Flow<String> {
        return getCurrent().map { provider -> provider.getUri(darkMode) }
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