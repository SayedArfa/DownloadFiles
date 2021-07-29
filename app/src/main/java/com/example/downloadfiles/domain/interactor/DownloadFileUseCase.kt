package com.example.downloadfiles.domain.interactor

import com.example.downloadfiles.domain.model.entity.File
import com.example.downloadfiles.domain.model.repo.FileRepo
import javax.inject.Inject

class DownloadFileUseCase @Inject constructor(private val repository: FileRepo) {
    fun downloadFile(file: File) {
        return repository.downloadFile(file)
    }
}