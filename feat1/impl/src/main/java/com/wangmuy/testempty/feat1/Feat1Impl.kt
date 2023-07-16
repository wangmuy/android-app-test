package com.wangmuy.testempty.feat1

import android.content.Context
import android.util.Log
import com.wangmuy.testempty.Const.DEBUG_TAG
import com.wangmuy.testempty.Item
import com.wangmuy.testempty.RemoteRepo
import kotlinx.coroutines.runBlocking

class Feat1Impl(val context: Context, val remoteRepo: RemoteRepo): IFeat1 {
    companion object {
        internal const val TAG = "Feat1Impl$DEBUG_TAG"
        private val DEFAULT_ITEM = Feat1Item(1)
    }

    override fun doFeat1(x: Int): Int {
        Log.d(TAG, "appName=${context.applicationInfo.name}, remoteRepo=$remoteRepo")
        runBlocking {
            Log.d(TAG, "all remoteRepos = ${remoteRepo.getAll()}")
        }
        return x*x
    }

    override fun myCachedItem(): Item {
        return DEFAULT_ITEM
    }
}