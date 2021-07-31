package com.example.nagwatask.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.downloadfiles.base.utils.Resource
import com.example.downloadfiles.base.viewmodel.BaseViewModel
import com.example.downloadfiles.domain.interactor.DownloadFileUseCase
import com.example.downloadfiles.domain.interactor.GetFilesUseCase
import com.example.downloadfiles.domain.model.entity.File
import com.example.downloadfiles.domain.model.entity.FileDownloadStatus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.IOException

class MainViewModel(
    val context: Context,
    private val getFilesUseCase: GetFilesUseCase,
    private val downloadFileUseCase: DownloadFileUseCase
) : BaseViewModel() {
    private val _filesLiveData = MutableLiveData<Resource<List<FileDownloadStatus>>>()
    val filesLiveData: LiveData<Resource<List<FileDownloadStatus>>> = _filesLiveData

    init {
        getFiles()
    }

    fun getFiles() {
        val disposable = getFilesUseCase.getFiles().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _filesLiveData.value = Resource.Success(it)
            }, {
                if (it is IOException) {
                    _filesLiveData.value = Resource.NetworkError(it)
                } else {
                    _filesLiveData.value = Resource.UnknownError(it)
                }
                it?.printStackTrace()
            })
        addDisposable(disposable)
    }

    fun downloadFile(file: File) {
        downloadFileUseCase.downloadFile(file)
    }
}