package com.wangmuy.modulartest.feat1

import org.koin.dsl.module

val feat1Module = module {
    single<IFeat1> { Feat1Impl(get()) }
}