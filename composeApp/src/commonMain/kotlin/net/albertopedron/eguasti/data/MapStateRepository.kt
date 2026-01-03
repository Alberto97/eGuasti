package net.albertopedron.eguasti.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.serialization.json.Json
import net.albertopedron.eguasti.data.model.AppMapState
import net.albertopedron.eguasti.tools.Files

class MapStateRepository(
    private val json: Json = Json
) {
    companion object {
        private const val FILE_NAME = "map_state.json"
    }

    private val localFile = Path(Files.root, FILE_NAME)

    suspend fun readState(): AppMapState? = withContext(Dispatchers.IO) {
        try {
            if (!SystemFileSystem.exists(localFile)) return@withContext null
            val content = SystemFileSystem.source(localFile).buffered().use { it.readString() }
            if (content.isEmpty()) return@withContext null

            json.decodeFromString<AppMapState>(content)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun writeState(state: AppMapState) = withContext(Dispatchers.IO) {
        try {
            val json = json.encodeToString(state)
            SystemFileSystem.sink(localFile).buffered().use { file ->
                file.writeString(json)
            }
        } catch (e: Exception) {
            // Handle error (optional)
        }
    }
}
