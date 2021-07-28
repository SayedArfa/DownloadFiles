package com.example.downloadfiles.base.di

import com.example.downloadfiles.domain.model.repo.FileRepo
import com.example.nagwatask.data.repo.FileRepoImp
import dagger.Binds
import dagger.Module

@Module
abstract class FileModule {
    @Binds
    abstract fun provideFileRepo(repoImp: FileRepoImp): FileRepo
}