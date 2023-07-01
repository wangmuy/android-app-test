package com.wangmuy.modulartest.feat3

import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.bind
import org.koin.dsl.module

internal val feat32Module = module {
    single { Feat3Impl2() } bind IFeat3::class
}

fun loadFeat32Module() {
    loadKoinModules(feat32Module)
}

fun unloadFeat32Module() {
    unloadKoinModules(feat32Module)
}