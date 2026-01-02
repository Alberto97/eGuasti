package net.albertopedron.eguasti.data

import config.Secrets.MAPTILER_API_KEY

interface MapProvider {
    fun getValue(): MapProviders
    fun isAvailable(): Boolean
    fun getConfig(darkMode: Boolean): MapConfig
}

sealed class MapConfig {
    data class Vector(val uri: String): MapConfig()
    data class Raster(
        val tiles: List<String>,
        val tileSize: Int,
        val minZoom: Int,
        val maxZoom: Int,
        val attributionHtml: String? = null
    ) : MapConfig()
}

class OpenStreetMapProvider : MapProvider {
    override fun getValue(): MapProviders  = MapProviders.OpenStreetMap

    override fun isAvailable(): Boolean = true

    override fun getConfig(darkMode: Boolean): MapConfig = MapConfig.Raster(
        tiles = listOf("https://tile.openstreetmap.org/{z}/{x}/{y}.png"),
        tileSize = 256,
        minZoom = 0,
        maxZoom = 19,
        attributionHtml = "<a href=\"https://www.openstreetmap.org/copyright\">&copy; OpenStreetMap contributors</a>"
    )
}

class MapTilerProvider : MapProvider {
    override fun getValue() = MapProviders.MapTiler

    override fun isAvailable(): Boolean = MAPTILER_API_KEY.isNotEmpty()

    override fun getConfig(darkMode: Boolean) = MapConfig.Vector(
        if (darkMode) "https://api.maptiler.com/maps/basic-v2-dark/style.json?key=${MAPTILER_API_KEY}"
        else "https://api.maptiler.com/maps/openstreetmap/style.json?key=${MAPTILER_API_KEY}"
    )
}

class OpenFreeMapProvider: MapProvider {
    override fun getValue() = MapProviders.OpenFreeMap

    override fun isAvailable(): Boolean = true

    override fun getConfig(darkMode: Boolean) = MapConfig.Vector(
        if (darkMode) "https://tiles.openfreemap.org/styles/dark"
        else "https://tiles.openfreemap.org/styles/bright"
    )
}