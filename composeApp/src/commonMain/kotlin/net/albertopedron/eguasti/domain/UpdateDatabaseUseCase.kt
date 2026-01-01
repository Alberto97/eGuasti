package net.albertopedron.eguasti.domain

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import net.albertopedron.eguasti.data.OutageRepository
import net.albertopedron.eguasti.data.Persistence
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class UpdateDatabaseUseCase(
    private val persistence: Persistence = Persistence,
    private val repository: OutageRepository = OutageRepository()
) {

    @OptIn(ExperimentalTime::class)
    suspend fun execute(): Boolean = withContext(Dispatchers.IO) {
        val result = repository.fetch()
        val list = result.getOrNull() ?: return@withContext false
        repository.save(list)
        persistence.setLastDbUpdate(Clock.System.now().toEpochMilliseconds())
        return@withContext true
    }
}