package com.example.downloadfiles.domain.model.repo

import com.example.downloadfiles.domain.model.entity.File
import com.example.downloadfiles.domain.model.entity.FileDownloadStatus
import io.reactivex.Observable

interface FileRepo {
    fun getFiles(): Observable<List<File>>
    fun getFilesDownloadStatus(): Observable<List<FileDownloadStatus>>
    fun downloadFile(file: File): Observable<FileDownloadStatus>
}