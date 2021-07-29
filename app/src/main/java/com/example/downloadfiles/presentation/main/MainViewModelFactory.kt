package com.example.nagwatask.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.downloadfiles.domain.interactor.GetFilesUseCase
import com.example.downloadfiles.domain.model.repo.FileRepo
import javax.inject.Inject

class MainViewModelFactory @Inject constructor(
    private val context: Context,
    private val getFilesUseCase: GetFilesUseCase
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(context, getFilesUseCase) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}