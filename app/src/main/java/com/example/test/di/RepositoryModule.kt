package com.example.test.di

import com.example.test.common.data.repository.ItemRepository
import com.example.test.common.data.repository.ItemRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<ItemRepository> { ItemRepositoryImpl(getOrNull(), getOrNull()) }
}