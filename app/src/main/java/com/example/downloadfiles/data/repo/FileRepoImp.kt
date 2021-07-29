package com.example.nagwatask.data.repo

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.downloadfiles.base.utils.DownloadWorker
import com.example.downloadfiles.base.utils.getFileNameFromFile
import com.example.downloadfiles.domain.model.entity.File
import com.example.downloadfiles.domain.model.repo.FileRepo
import com.example.nagwatask.data.datasource.FileLocalDataSource
import io.reactivex.Observable
import javax.inject.Inject

class FileRepoImp @Inject constructor(
    private val localDataSource: FileLocalDataSource,
    private val context: Context
) : FileRepo {
    override fun getFiles(): Observable<List<File>> {
        return localDataSource.getFiles()
    }

    override fun downloadFile(file: File) {
        val workManager = WorkManager.getInstance(context)

        val data = workDataOf(
            DownloadWorker.KEY_INPUT_URL to file.url,
            DownloadWorker.KEY_OUTPUT_FILE_NAME to getFileNameFromFile(file)
        )

        val downloadWorker = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(data)
            .addTag(file.id.toString())
            .build()
        workManager.beginUniqueWork(file.id.toString(), ExistingWorkPolicy.REPLACE, downloadWorker)
            .enqueue()
    }
}