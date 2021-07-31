package com.example.downloadfiles.domain.interactor

import android.util.Log
import com.example.downloadfiles.domain.model.entity.File
import com.example.downloadfiles.domain.model.entity.FileDownloadStatus
import com.example.downloadfiles.domain.model.repo.FileRepo
import io.reactivex.Observable
import javax.inject.Inject

class DownloadFileUseCase @Inject constructor(private val repository: FileRepo) {
    fun downloadFile(file: File): Observable<FileDownloadStatus> {
        var retryCount = 0
        return repository.downloadFile(file).retryUntil {
            retryCount++
            Log.d("retryCount",retryCount.toString())
            retryCount >= 3
        }
    }
}