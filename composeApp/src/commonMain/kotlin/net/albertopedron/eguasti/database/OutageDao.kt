package net.albertopedron.eguasti.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface OutageDao {
    @Query("SELECT * FROM outages")
    fun getAll(): Flow<List<OutageEntity>>

    @Query("SELECT * FROM outages WHERE id = :id LIMIT 1")
    suspend fun get(id: Int): OutageEntity?

    @Upsert
    suspend fun add(outages: List<OutageEntity>)

    @Query("DELETE FROM outages")
    suspend fun clear()

    @Transaction
    suspend fun clearAndAdd(outages: List<OutageEntity>) {
        clear()
        add(outages)
    }
}