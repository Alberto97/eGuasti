package net.albertopedron.eguasti.data

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import net.albertopedron.eguasti.tools.Files

@Serializable
data class TrackedOutage(
    val id: Int,
    val lastUpdate: String
)

class OutageTracker() {
    companion object {
        private const val FILE_NAME = "tracked_outages.json"
    }

    private val localFile = Path(Files.root, FILE_NAME)
    private val json = Json { prettyPrint = true }

    private var outages: MutableList<TrackedOutage> = mutableListOf()

    init {
        load()
    }

    private fun load() {
        if (SystemFileSystem.exists(localFile)) {
            val text = SystemFileSystem.source(localFile).buffered().use { it.readString() }
            if (text.isEmpty()) return
            outages = json.decodeFromString(text)
        }
    }

    private fun save() {
        val path = Path(Files.root, FILE_NAME)
        SystemFileSystem.sink(path).buffered().use { file ->
            file.writeString(json.encodeToString(outages))
        }
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
