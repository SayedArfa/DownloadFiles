package com.example.nagwatask.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.downloadfiles.base.viewmodel.BaseViewModel
import com.example.downloadfiles.domain.interactor.DownloadFileUseCase
import com.example.downloadfiles.domain.interactor.GetFilesUseCase
import com.example.downloadfiles.domain.model.entity.File
import com.example.downloadfiles.domain.model.entity.FileDownloadStatus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainViewModel(
    val context: Context,
    private val getFilesUseCase: GetFilesUseCase,
    private val downloadFileUseCase: DownloadFileUseCase
) : BaseViewModel() {
    private val _filesLiveData = MutableLiveData<List<FileDownloadStatus>>()
    val filesLiveData: LiveData<List<FileDownloadStatus>> = _filesLiveData

    init {
        getFiles()
    }

    private fun getFiles() {
        val disposable = getFilesUseCase.getFiles().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _filesLiveData.value = it
            }, {
                it?.printStackTrace()
            })
        addDisposable(disposable)
    }

    fun downloadFile(file: File) {
        downloadFileUseCase.downloadFile(file)
    }
}