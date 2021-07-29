package com.example.downloadfiles.domain.interactor

import com.example.downloadfiles.domain.model.entity.FileDownloadStatus
import com.example.downloadfiles.domain.model.repo.FileRepo
import io.reactivex.Observable
import javax.inject.Inject

class GetFilesUseCase @Inject constructor(
    private val repository: FileRepo
) {

    fun getFiles(): Observable<List<FileDownloadStatus>> {
        return repository.getFilesDownloadStatus()
    }
}