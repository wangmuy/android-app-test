package com.wangmuy.testempty.di

import android.util.Log
import com.wangmuy.testempty.Const.DEBUG_TAG
import com.wangmuy.testempty.Item
import com.wangmuy.testempty.RemoteRepo
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import kotlinx.coroutines.runBlocking

@Module
class BaseItemsModule {
    companion object {
        private const val TAG = "BaseItemsModule$DEBUG_TAG"
    }

    @Provides
    @ElementsIntoSet
    fun provideCachedItems(remoteRepo: RemoteRepo): Set<Item> {
        Log.d(TAG, "remoteRepo=$remoteRepo")
        return runBlocking { remoteRepo.getAll().toSet() }
    }
}