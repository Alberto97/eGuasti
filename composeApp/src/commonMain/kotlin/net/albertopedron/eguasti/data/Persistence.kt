package net.albertopedron.eguasti.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object Persistence {
    private val dataStore: DataStore<Preferences> = createDataStore()

    private val lastDbUpdateKey = longPreferencesKey("last_db_update")
    private val currentMapProviderKey = stringPreferencesKey("current_map_provider")
    private val experimentalSettingsEnabledKey = booleanPreferencesKey("experimental_settings_enabled")
    private val trackOutagesEnabledKey = booleanPreferencesKey("track_outages_enabled")
    private val fetchUsingProtocolBuffersKey = booleanPreferencesKey("fetch_using_protocol_buffers")

    fun getLastDbUpdate(): Flow<Long> {
        return dataStore.data
            .map { settings -> settings[lastDbUpdateKey] ?: 0 }
    }

    suspend fun setLastDbUpdate(value: Long) {
        dataStore.edit { settings ->
            settings[lastDbUpdateKey] = value
        }
    }

    fun getCurrentMapProvider(): Flow<String?> {
        return dataStore.data.map { settings -> settings[currentMapProviderKey] }
    }

    suspend fun setCurrentMapProvider(value: String) {
        dataStore.edit { settings ->
            settings[currentMapProviderKey] = value
        }
    }

    fun getExperimentalSettingsEnabled(): Flow<Boolean> {
        return dataStore.data
            .map { settings -> settings[experimentalSettingsEnabledKey] ?: false }
    }

    suspend fun setExperimentalSettingsEnabled(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[experimentalSettingsEnabledKey] = enabled
        }
    }

    fun getTrackOutagesEnabled(): Flow<Boolean> {
        return dataStore.data
            .map { settings -> settings[trackOutagesEnabledKey] ?: false }
    }

    suspend fun setTrackOutagesEnabled(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[trackOutagesEnabledKey] = enabled
        }
    }

    fun getFetchUsingProtocolBuffers(): Flow<Boolean> {
        return dataStore.data
            .map { settings -> settings[fetchUsingProtocolBuffersKey] ?: true }
    }

    suspend fun setFetchUsingProtocolBuffers(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[fetchUsingProtocolBuffersKey] = enabled
        }
    }
}
