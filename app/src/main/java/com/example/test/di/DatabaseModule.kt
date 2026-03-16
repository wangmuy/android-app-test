package com.example.test.di

import androidx.room.Room
import com.example.test.common.data.local.database.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

//    single { get<AppDatabase>().itemDao() }
}