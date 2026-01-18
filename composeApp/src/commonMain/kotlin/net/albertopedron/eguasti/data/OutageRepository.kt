package net.albertopedron.eguasti.data

import com.skydoves.sandwich.getOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import net.albertopedron.eguasti.data.mapper.OutageMapper
import net.albertopedron.eguasti.data.model.Cause
import net.albertopedron.eguasti.data.model.Outage
import net.albertopedron.eguasti.data.model.toSql
import net.albertopedron.eguasti.database.DatabaseProvider
import net.albertopedron.eguasti.database.OutageDao
import net.albertopedron.eguasti.network.ApiContainer
import net.albertopedron.eguasti.network.EnelApi

class OutageRepository(
    private val client: EnelApi = ApiContainer.enelApi,
    private val mapper: OutageMapper = OutageMapper(),
    private val dao: OutageDao = DatabaseProvider.instance.outageDao(),
    private val persistence: Persistence = Persistence,
) {

    fun getAll(): Flow<List<Outage>> {
        return dao.getAll().map { entities -> entities.map { mapper.fromEntity(it) } }
    }

    suspend fun get(id: Int): Outage? = withContext(Dispatchers.IO) {
        return@withContext dao.get(id)?.let { mapper.fromEntity(it) }
    }

    suspend fun save(outages: List<Outage>) = withContext(Dispatchers.IO) {
        val entities = outages.map { mapper.toEntity(it) }
        dao.clearAndAdd(entities)
    }

    suspend fun fetch(): Result<List<Outage>> = withContext(Dispatchers.IO) {

        @Suppress("SimplifiableCallChain", "SpellCheckingInspection")
        val where = Cause.entries
            .map { cause -> "causa_disalimentazione = '${cause.toSql()}'" }
            .joinToString(separator = " or ")

        return@withContext query(where)
    }

    private suspend fun query(where: String): Result<List<Outage>> {
        val fetchUsingProtocolBuffers = persistence.getFetchUsingProtocolBuffers().firstOrNull() ?: true
        if (fetchUsingProtocolBuffers) {
            val result = client.queryPbf(where).getOrNull() ?: return Result.failure(Exception("Query failed"))
            val featureResult = result.queryResult.featureResult ?: return Result.failure(Exception("Feature result is null"))
            val res = mapper.mapFeatures(featureResult)
            return Result.success(res)
        } else {
            val result = client.queryJson(where).getOrNull() ?: return Result.failure(Exception("Query failed"))
            val res = mapper.mapFeatures(result)
            return Result.success(res)
        }
    }
}
