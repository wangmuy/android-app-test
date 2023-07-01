package com.wangmuy.modulartest

import com.wangmuy.modulartest.feat1.feat1Module
import com.wangmuy.modulartest.feat2.feat2Module
import com.wangmuy.modulartest.feat3.feat3Module
import org.koin.dsl.module

val appModule = module {
    includes(listOf(
        feat1Module,
        feat2Module,
        feat3Module,
    ))
}