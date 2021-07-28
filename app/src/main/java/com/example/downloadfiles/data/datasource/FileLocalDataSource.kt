package com.example.nagwatask.data.datasource

import android.content.Context
import com.example.downloadfiles.base.utils.readAssetsFileAsString
import com.example.downloadfiles.domain.model.entity.File
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.Single
import java.lang.reflect.Type
import javax.inject.Inject

class FileLocalDataSource @Inject constructor(private val context: Context) {
    fun getFiles(): Observable<List<File>> {
        val json = readAssetsFileAsString(context, "getListOfFilesResponse.json")
        val typeToken: Type = object :
            TypeToken<List<File>>() {}.type
        return Single.create<List<File>> {
            if (!it.isDisposed)
                it.onSuccess(Gson().fromJson(json, typeToken))
        }.toObservable()
    }
}