package com.wangmuy.testempty.di

import com.wangmuy.testempty.MainActivity
import com.wangmuy.testempty.di.scope.ActivityScope
import com.wangmuy.testempty.feat1.Feat1Module
import com.wangmuy.testempty.feat2.Feat2Module
import com.wangmuy.testempty.feat3.Feat32Module
import com.wangmuy.testempty.feat3.Feat3Module
import dagger.Component

// https://medium.com/@ffvanderlaan/dynamic-feature-and-regular-modules-using-dagger2-12a7edcec1ff
@ActivityScope
@Component(
    modules = [
        BaseItemsModule::class,
        Feat1Module::class,
        Feat2Module::class,
        Feat3Module::class,
        Feat32Module::class,
        MainActivityModule::class
    ],
    dependencies = [ BaseComponent::class ] // outside ActivityScope
)
interface MainActivityComponent {
    fun inject(mainActivity: MainActivity)
}