package com.ams.downloadfiles.data.source.remote

import com.ams.downloadfiles.data.source.remote.network.FileAPIService
import javax.inject.Inject

class FileRemoteDataSource @Inject constructor(private val service: FileAPIService) {
    fun getFiles() = service.getFiles()
}