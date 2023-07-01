package com.wangmuy.modulartest.feat3

import org.koin.dsl.bind
import org.koin.dsl.module

val feat3Module = module {
    single { Feat3Impl() } bind IFeat3::class
}