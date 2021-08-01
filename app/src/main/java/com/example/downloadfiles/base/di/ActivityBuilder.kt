package com.example.downloadfiles.base.di


import com.example.downloadfiles.presentation.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [FileModule::class])
    abstract fun bindMainActivity(): MainActivity
}