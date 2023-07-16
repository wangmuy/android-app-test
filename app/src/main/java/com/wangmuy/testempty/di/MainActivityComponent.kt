package com.wangmuy.testempty.di

import com.wangmuy.testempty.MainActivity
import com.wangmuy.testempty.di.scope.ActivityScope
import com.wangmuy.testempty.feat1.Feat1Module
import dagger.Component

@ActivityScope
@Component(
    modules = [
        BaseItemsModule::class,
        Feat1Module::class,
        MainActivityModule::class
    ],
    dependencies = [ BaseComponent::class ] // outside ActivityScope
)
interface MainActivityComponent {
    fun inject(mainActivity: MainActivity)
}