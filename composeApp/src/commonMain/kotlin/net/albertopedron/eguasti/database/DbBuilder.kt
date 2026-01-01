package net.albertopedron.eguasti.database

import androidx.room.RoomDatabase

expect object DbBuilder {
    fun getBuilder(): RoomDatabase.Builder<OutageDatabase>
}