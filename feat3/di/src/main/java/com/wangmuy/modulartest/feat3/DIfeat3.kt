package com.wangmuy.modulartest.feat3

import org.koin.dsl.module

val feat3Module = module {
    factory<IFeat3> { Feat3Impl() }
}