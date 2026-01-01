package net.albertopedron.eguasti.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import net.albertopedron.eguasti.tools.Files
import okio.Path.Companion.toPath

/**
 *   Gets the singleton DataStore instance, creating it if necessary.
 */
fun createDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            kotlinx.io.files.Path(Files.root, dataStoreFileName).toString()
                .toPath()
        }
    )

internal const val dataStoreFileName = "dice.preferences_pb"