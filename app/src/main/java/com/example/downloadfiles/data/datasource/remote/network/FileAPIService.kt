package com.ams.downloadfiles.data.source.remote.network

import com.example.downloadfiles.domain.model.entity.File
import io.reactivex.Observable
import retrofit2.http.GET

interface FileAPIService {

    @GET("v3/a55711dd-645f-4ce3-8e71-1342c0c724ff")
    fun getFiles(): Observable<List<File>>

}