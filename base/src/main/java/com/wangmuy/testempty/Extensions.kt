package com.wangmuy.testempty

import android.app.Activity
import com.wangmuy.testempty.di.BaseComponentProvider

fun Activity.baseComponent() = (applicationContext as? BaseComponentProvider)?.provideBaseComponent()
    ?: throw IllegalStateException("BaseComponentProvider not implemented $applicationContext")
