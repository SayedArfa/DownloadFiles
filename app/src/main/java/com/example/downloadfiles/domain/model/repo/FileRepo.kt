package com.example.downloadfiles.domain.model.repo

import com.example.downloadfiles.domain.model.entity.File
import io.reactivex.Observable

interface FileRepo {
    fun getFiles(): Observable<List<File>>
    fun downloadFile(file: File)
}