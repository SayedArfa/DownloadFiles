package com.example.downloadfiles.domain.model.entity

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import com.example.downloadfiles.base.utils.DownloadWorker
import com.example.downloadfiles.base.utils.isFileExist

data class FileDownloadStatus(val file: File) {
    var downloadStatus = DownloadStatus.NOT_DOWNLOADED
    var progress = 0

    companion object {
        suspend fun getFileDownloadStatus(context: Context, file: File): FileDownloadStatus {

            val info =
                WorkManager.getInstance(context).getWorkInfosForUniqueWork(file.id.toString())
                    .await().firstOrNull()

            return getFileDownloadStatus(context, file, info)

        }

        fun getFileDownloadStatus(
            context: Context,
            file: File,
            workInfo: WorkInfo?
        ): FileDownloadStatus {
            val isFileExist = isFileExist(file, context)
            return workInfo?.let {
                when (it.state) {
                    WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING -> {
                        FileDownloadStatus(file).apply {
                            downloadStatus = DownloadStatus.DOWNLOADING
                            progress =
                                workInfo.progress.getInt(
                                    DownloadWorker.KEY_DOWNLOAD_WORK_PROGRESS,
                                    0
                                )
                        }
                    }

                    WorkInfo.State.SUCCEEDED -> {
                        if (isFileExist) {
                            FileDownloadStatus(file).apply {
                                downloadStatus = DownloadStatus.DOWNLOADED
                            }
                        } else {
                            FileDownloadStatus(file).apply {
                                downloadStatus = DownloadStatus.NOT_DOWNLOADED
                            }
                        }
                    }
                    WorkInfo.State.FAILED, WorkInfo.State.BLOCKED, WorkInfo.State.CANCELLED -> {
                        FileDownloadStatus(file).apply {
                            downloadStatus = DownloadStatus.FAILED
                        }
                    }

                }
            } ?: FileDownloadStatus(file).apply {
                downloadStatus = DownloadStatus.NOT_DOWNLOADED
            }
        }

        enum class DownloadStatus {
            DOWNLOADED, DOWNLOADING, NOT_DOWNLOADED, FAILED
        }
    }


}
