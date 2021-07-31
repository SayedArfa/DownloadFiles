package com.example.downloadfiles.base.di

import com.ams.downloadfiles.data.source.remote.network.FileAPIService
import com.example.downloadfiles.base.utils.ServiceGenerator
import dagger.Module
import dagger.Provides

@Module
class AppModule {
    @Provides
    fun provideFileServiceAPIs(): FileAPIService =
        ServiceGenerator().createService(FileAPIService::class.java)
}