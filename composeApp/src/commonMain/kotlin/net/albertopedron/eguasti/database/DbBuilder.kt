package net.albertopedron.eguasti.database

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteDriver

expect object DbBuilder {
    fun getDriver(): SQLiteDriver
    fun getBuilder(): RoomDatabase.Builder<OutageDatabase>
}