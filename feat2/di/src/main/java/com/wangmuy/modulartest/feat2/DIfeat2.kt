package com.wangmuy.modulartest.feat2

import org.koin.dsl.module

val feat2Module = module {
    factory<IFeat2> { Feat2Impl(get()) }
}