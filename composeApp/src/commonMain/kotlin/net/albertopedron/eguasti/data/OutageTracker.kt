package net.albertopedron.eguasti.data

import io.ktor.utils.io.core.writeText
import io.ktor.utils.io.readText
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import net.albertopedron.eguasti.tools.Files

@Serializable
data class TrackedOutage(
    val id: Int,
    val lastUpdate: String
)

class OutageTracker() {
    private val fileName = "tracked_outages.json"
    private val json = Json { prettyPrint = true }

    private var outages: MutableList<TrackedOutage> = mutableListOf()

    init {
        load()
    }

    private val localFile = Path(Files.root, fileName)

    private fun load() {
        return
        if (SystemFileSystem.exists(localFile)) {
            val text = SystemFileSystem.source(localFile).buffered().readText()
            outages = json.decodeFromString(text)
        }
    }

    private fun save() {
        val file = SystemFileSystem.sink(localFile).buffered()
        file.writeText(json.encodeToString(outages))
    }

    fun track(outage: TrackedOutage) {
        outages.removeAll { it.id == outage.id }
        outages.add(outage)
        save()
    }

    fun untrack(id: Int) {
        outages.removeAll { it.id == id }
        save()
    }

    fun isTracking(id: Int): Boolean {
        return outages.any { it.id == id }
    }

    fun getTracked(): List<TrackedOutage> {
        return outages
    }

    fun getLastUpdate(id: Int): String? {
        return outages.find { it.id == id }?.lastUpdate
    }
}
