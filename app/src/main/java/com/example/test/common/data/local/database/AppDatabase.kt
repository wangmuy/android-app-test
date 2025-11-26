package com.example.test.common.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.test.common.data.local.dao.ItemDao
import com.example.test.common.data.local.entity.ItemEntity

@Database(
    entities = [ItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        const val DATABASE_NAME = "app_database"
    }
}