package com.wangmuy.testempty.feat1

import android.content.Context
import com.wangmuy.testempty.Item
import com.wangmuy.testempty.RemoteRepo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoSet

// https://developer.android.com/training/dependency-injection/dagger-multi-module#best-practices
// For Gradle modules that are meant to be utilities or helpers and don't need to build a graph
// (that's why you'd need a Dagger component), create and expose public Dagger modules with
// @Provides and @Binds methods of those classes that don't support constructor injection.
@Module
class Feat1Module {

    @Reusable
    @Provides
    fun provideFeat1(context: Context, remoteRepo: RemoteRepo): IFeat1 = Feat1Impl(context, remoteRepo)

    @Reusable
    @Provides
    @IntoSet
    fun provideFeat1Item(feat1: IFeat1): Item = feat1.myCachedItem()
}