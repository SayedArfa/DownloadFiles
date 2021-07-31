package com.example.nagwatask.data.repo

import android.content.Context
import androidx.work.*
import com.ams.downloadfiles.data.source.remote.FileRemoteDataSource
import com.example.downloadfiles.base.utils.DownloadWorker
import com.example.downloadfiles.base.utils.getFileNameFromFile
import com.example.downloadfiles.data.datasource.local.FileLocalDataSource
import com.example.downloadfiles.domain.model.entity.File
import com.example.downloadfiles.domain.model.entity.FileDownloadStatus
import com.example.downloadfiles.domain.model.repo.FileRepo
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FileRepoImp @Inject constructor(
    private val localDataSource: FileLocalDataSource,
    private val remoteDataSource: FileRemoteDataSource,
    private val context: Context
) : FileRepo {
    override fun getFiles(): Observable<List<File>> {
        return remoteDataSource.getFiles()
//        return localDataSource.getFiles()
    }

    private val workManager = WorkManager.getInstance(context)
    override fun getFilesDownloadStatus(): Observable<List<FileDownloadStatus>> {
        return getFiles().concatMap { fileList ->
            Observable.create { emitter ->
                GlobalScope.launch(Dispatchers.IO) {
                    var list = mutableListOf<FileDownloadStatus>()
                    for (file in fileList) {
                        list.add(FileDownloadStatus.getFileDownloadStatus(context, file))
                    }
                    emitter.onNext(list)
                    list.forEach { downloadStatus ->
                        withContext(Dispatchers.Main) {
                            workManager.getWorkInfosForUniqueWorkLiveData(downloadStatus.file.id.toString())
                                .observeForever { workInfo ->
                                    GlobalScope.launch(Dispatchers.IO) {
                                        workInfo.firstOrNull()?.let {
                                            val newStatus =
                                                FileDownloadStatus.getFileDownloadStatus(
                                                    context,
                                                    downloadStatus.file,
                                                    it
                                                )
                                            val newList = mutableListOf<FileDownloadStatus>()
                                            newList.addAll(list)
                                            newList[list.indexOf(downloadStatus)] = newStatus
                                            emitter.onNext(newList)
                                            list = newList
                                        }
                                    }
                                }
                        }
                    }
                }
            }
        }
    }

    override fun downloadFile(file: File): Observable<FileDownloadStatus> {
        return Observable.create { emitter ->
            val workManager = WorkManager.getInstance(context)

            val data = workDataOf(
                DownloadWorker.KEY_INPUT_URL to file.url,
                DownloadWorker.KEY_OUTPUT_FILE_NAME to getFileNameFromFile(file)
            )

            val downloadWorker = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(data)
                .addTag(file.id.toString())
                .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.SECONDS)
                .build()
            workManager.beginUniqueWork(
                file.id.toString(),
                ExistingWorkPolicy.REPLACE,
                downloadWorker
            )
                .enqueue()
            workManager.getWorkInfoByIdLiveData(downloadWorker.id).observeForever {
                it?.let {
                    if (it.state in listOf(WorkInfo.State.FAILED, WorkInfo.State.BLOCKED))
                        emitter.onError(Exception())
                    else
                        emitter.onNext(
                            FileDownloadStatus.getFileDownloadStatus(
                                context,
                                file,
                                it
                            )
                        )
                }
            }
        }

    }
}