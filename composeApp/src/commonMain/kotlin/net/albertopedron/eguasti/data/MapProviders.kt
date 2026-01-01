package net.albertopedron.eguasti.data

import config.Secrets.MAPTILER_API_KEY

interface MapProvider {
    fun getValue(): MapProviders
    fun isAvailable(): Boolean
    fun getUri(darkMode: Boolean): String
}

class MapTilerProvider : MapProvider {
    override fun getValue() = MapProviders.MapTiler

    override fun isAvailable(): Boolean = MAPTILER_API_KEY.isNotEmpty()

    override fun getUri(darkMode: Boolean): String {
        return if (darkMode) "https://api.maptiler.com/maps/basic-v2-dark/style.json?key=${MAPTILER_API_KEY}"
        else "https://api.maptiler.com/maps/openstreetmap/style.json?key=${MAPTILER_API_KEY}"
    }
}

class OpenFreeMapProvider: MapProvider {
    override fun getValue() = MapProviders.OpenFreeMap

    override fun isAvailable(): Boolean = true

    override fun getUri(darkMode: Boolean): String = if (darkMode) "https://tiles.openfreemap.org/styles/dark" else "https://tiles.openfreemap.org/styles/bright"
}