package com.example.downloadfiles.domain.interactor

import android.content.Context
import androidx.work.WorkManager
import com.example.downloadfiles.domain.model.entity.FileDownloadStatus
import com.example.downloadfiles.domain.model.repo.FileRepo
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFilesUseCase @Inject constructor(
    private val repository: FileRepo,
    private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)
    fun getFiles(): Observable<List<FileDownloadStatus>> {
        return repository.getFiles().concatMap { fileList ->
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
                                                FileDownloadStatus.Companion.getFileDownloadStatus(
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
}