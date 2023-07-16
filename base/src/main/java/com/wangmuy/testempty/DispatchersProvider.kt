package com.wangmuy.testempty

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

open class DispatchersProvider {
    open val Main: CoroutineContext by lazy { Dispatchers.Main }
    open val IO: CoroutineContext by lazy { Dispatchers.IO }
    open val Default: CoroutineContext by lazy { Dispatchers.Default }
}