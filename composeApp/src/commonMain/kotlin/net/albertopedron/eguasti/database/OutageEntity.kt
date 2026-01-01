package net.albertopedron.eguasti.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outages")
data class OutageEntity(
    @PrimaryKey val id: Int,
    val start: String,
    val expectedRestore: String,
    val lastUpdate: String,
    val place: String,
    val latitude: Double,
    val longitude: Double,
    val offlineCustomers: Int,
    val cause: String
)
