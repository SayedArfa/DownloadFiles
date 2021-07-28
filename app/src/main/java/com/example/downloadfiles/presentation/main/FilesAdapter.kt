package com.example.nagwatask.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.downloadfiles.R
import com.example.downloadfiles.databinding.FileItemviewBinding
import com.example.downloadfiles.domain.model.entity.File
import com.example.downloadfiles.domain.model.entity.FileDownloadStatus


class FilesAdapter : RecyclerView.Adapter<FilesAdapter.FileViewHolder>() {

    var onOpenClicked: ((FileDownloadStatus) -> Unit)? = null
    var onDownloadClicked: ((File) -> Unit)? = null

    private val differCallback = object : DiffUtil.ItemCallback<FileDownloadStatus>() {
        override fun areItemsTheSame(
            oldItem: FileDownloadStatus,
            newItem: FileDownloadStatus
        ): Boolean {
            return oldItem.file.id == newItem.file.id
        }

        override fun areContentsTheSame(
            oldItem: FileDownloadStatus,
            newItem: FileDownloadStatus
        ): Boolean {
            return oldItem.file.id == newItem.file.id && oldItem.downloadStatus == newItem.downloadStatus && oldItem.progress == newItem.progress
        }
    }

    val differ = AsyncListDiffer(this, differCallback)


    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.file_itemview, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }


    inner class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val viewBinding = FileItemviewBinding.bind(itemView)
        fun bind(fileDownloadStatus: FileDownloadStatus) {
            viewBinding.nameTv.text = fileDownloadStatus.file.name
            when (fileDownloadStatus.downloadStatus) {
                FileDownloadStatus.Companion.DownloadStatus.DOWNLOADED -> {
                    viewBinding.progress.visibility = View.GONE
                    viewBinding.actionButton.apply {
                        visibility = View.VISIBLE
                        text = "Open"
                        setOnClickListener {
                            onOpenClicked?.invoke(fileDownloadStatus)
                        }
                    }
                }
                FileDownloadStatus.Companion.DownloadStatus.DOWNLOADING -> {
                    viewBinding.actionButton.visibility = View.GONE
                    viewBinding.progress.visibility = View.VISIBLE
                    viewBinding.progress.progress = fileDownloadStatus.progress.toFloat()
                    viewBinding.actionButton.setOnClickListener(null)
                }
                FileDownloadStatus.Companion.DownloadStatus.NOT_DOWNLOADED -> {
                    viewBinding.progress.visibility = View.GONE
                    viewBinding.actionButton.apply {
                        visibility = View.VISIBLE
                        text = "Download"
                        setOnClickListener {
                            onDownloadClicked?.invoke(fileDownloadStatus.file)
                        }
                    }
                }
            }
        }
    }
}