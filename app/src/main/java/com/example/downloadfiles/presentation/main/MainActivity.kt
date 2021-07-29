package com.example.downloadfiles.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.downloadfiles.base.app.MyApp
import com.example.downloadfiles.base.utils.getFileNameFromFile
import com.example.downloadfiles.base.utils.getRootFile
import com.example.downloadfiles.base.utils.openFile
import com.example.downloadfiles.databinding.ActivityMainBinding
import com.example.nagwatask.ui.main.FilesAdapter
import com.example.nagwatask.ui.main.MainViewModel
import com.example.nagwatask.ui.main.MainViewModelFactory
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private val filesAdapter = FilesAdapter()
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var layoutManager: LinearLayoutManager

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApp).applicationComponent.inject(this)
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        mainViewModel = ViewModelProvider(
            this,
            viewModelFactory
        ).get(MainViewModel::class.java)

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
            it?.let { fileList ->
                filesAdapter.differ.submitList(fileList)
            }
        }
    }
}