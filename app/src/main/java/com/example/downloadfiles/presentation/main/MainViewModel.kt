package com.example.nagwatask.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.downloadfiles.base.utils.Event
import com.example.downloadfiles.domain.model.entity.File
import com.example.downloadfiles.domain.model.entity.FileDownloadStatus
import com.example.downloadfiles.domain.model.repo.FileRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(val context: Context, private val fileRepo: FileRepo) : ViewModel() {
    private val _filesLiveData = MutableLiveData<List<FileDownloadStatus>>()
    val filesLiveData: LiveData<List<FileDownloadStatus>> = _filesLiveData

    private val _downloadStatusLiveData = MutableLiveData<Event<FileDownloadStatus>>()
    val downloadStatusLiveData: LiveData<Event<FileDownloadStatus>> = _downloadStatusLiveData

    private val _downloadFailedLiveData = MutableLiveData<Event<FileDownloadStatus>>()
    val downloadFailedLiveData: LiveData<Event<FileDownloadStatus>> = _downloadFailedLiveData

    private val compositeDisposable = CompositeDisposable()

    init {
        getFiles()
    }

    private val workManager = WorkManager.getInstance(context)
    private fun getFiles() {
        val disposable = fileRepo.getFiles().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewModelScope.launch {
                    var list = mutableListOf<FileDownloadStatus>()
                    withContext(Dispatchers.IO) {
                        for (file in it) {
                            list.add(FileDownloadStatus.getFileDownloadStatus(context, file))
                        }
                    }
                    _filesLiveData.value = list
                    list.forEach { downloadStatus ->
                        workManager.getWorkInfosForUniqueWorkLiveData(downloadStatus.file.id.toString())
                            .observeForever { workInfo ->
                                viewModelScope.launch(Dispatchers.IO) {
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
                                        withContext(Dispatchers.Main) {
                                            _filesLiveData.value = newList
                                        }
                                        list = newList
                                    }
                                }
                            }
                    }
                }

            }, {
                it?.printStackTrace()
            })
        compositeDisposable.add(disposable)
    }


    fun downloadFile(file: File) {
        fileRepo.downloadFile(file)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}