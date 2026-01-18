package net.albertopedron.eguasti.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.AndroidSQLiteDriver
import net.albertopedron.eguasti.EGuastiApplication

actual object DbBuilder {
    actual fun getDriver(): SQLiteDriver = AndroidSQLiteDriver()

    actual fun getBuilder(): RoomDatabase.Builder<OutageDatabase> {
        val appContext = EGuastiApplication.instance
        val dbFile = appContext.getDatabasePath("outages_db.db")
        return Room.databaseBuilder<OutageDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}