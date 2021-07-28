package com.example.downloadfiles.base.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.example.downloadfiles.base.di.AppComponent
import com.example.downloadfiles.base.di.DaggerAppComponent


class MyApp : MultiDexApplication() {
    val applicationComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}