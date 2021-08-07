package com.example.downloadfiles.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.downloadfiles.base.utils.*
import com.example.downloadfiles.base.viewmodel.ViewModelFactory
import com.example.downloadfiles.databinding.ActivityMainBinding
import com.example.nagwatask.ui.main.FilesAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory<MainViewModel>
    private val filesAdapter = FilesAdapter()
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var layoutManager: LinearLayoutManager
    private var errorSnackbar: Snackbar? = null

    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        mainViewModel.downloadErrorLiveData.observe(this, EventObserver {
            it?.let {
                errorSnackbar = Snackbar.make(
                    viewBinding.root,
                    "Failed to download File ${it.file.name}",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Dismiss") {
                    errorSnackbar?.dismiss()
                }
                errorSnackbar?.show()
            } ?: kotlin.run {
                errorSnackbar?.dismiss()
            }
        })

        filesAdapter.onDownloadClicked = {
            mainViewModel.downloadFile(it)
        }

        filesAdapter.onOpenClicked = {
            openFile(
                this,
                getRootFile(this).absolutePath + "/" + getFileNameFromFile(it.file)
            )
        }

        layoutManager = LinearLayoutManager(this)
        viewBinding.FilesRecyclerView.layoutManager = layoutManager
        viewBinding.FilesRecyclerView.adapter = filesAdapter
        mainViewModel.filesLiveData.observe(this) {
            it?.let { resource ->
                when (resource) {
                    is Resource.Success -> {
                        filesAdapter.differ.submitList(resource.data)
                    }
                    is Resource.NetworkError -> {
                        filesAdapter.differ.submitList(listOf())
                        Snackbar.make(
                            viewBinding.root,
                            "No Internet connection",
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction("Retry") {
                            mainViewModel.getFiles()
                        }.show()
                    }
                    is Resource.UnknownError -> {
                        filesAdapter.differ.submitList(listOf())
                        Snackbar.make(viewBinding.root, "Unknown error", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry") {
                                mainViewModel.getFiles()
                            }.show()
                    }
                }

            }
        }
    }
}