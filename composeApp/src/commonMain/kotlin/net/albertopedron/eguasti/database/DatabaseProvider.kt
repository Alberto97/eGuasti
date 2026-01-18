package net.albertopedron.eguasti.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO


object DatabaseProvider {
    val instance: OutageDatabase = DbBuilder.getBuilder()
        .setDriver(DbBuilder.getDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}