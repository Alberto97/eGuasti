package net.albertopedron.eguasti.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [OutageEntity::class], version = 1, exportSchema = false)
@ConstructedBy(OutageDatabaseConstructor::class)
abstract class OutageDatabase : RoomDatabase() {
    abstract fun outageDao(): OutageDao
}

@Suppress("KotlinNoActualForExpect", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object OutageDatabaseConstructor : RoomDatabaseConstructor<OutageDatabase> {
    override fun initialize(): OutageDatabase
}


