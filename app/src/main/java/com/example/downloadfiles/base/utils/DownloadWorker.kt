package com.example.downloadfiles.base.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.downloadfiles.R
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    override suspend fun doWork(): Result {
        val inputUrl = inputData.getString(KEY_INPUT_URL)
            ?: return Result.failure()
        val outputFile = inputData.getString(KEY_OUTPUT_FILE_NAME)
            ?: return Result.failure()
        // Mark the Worker as important
        val progress = 0
        setForeground(createForegroundInfo((inputUrl + outputFile).hashCode(), progress))
        try {
            download(inputUrl, outputFile)
        } catch (e: Exception) {
            deleteFileRecursive(getRootFile(applicationContext).path + "/" + outputFile)
            return Result.failure()
        }
        return Result.success()
    }


    private suspend fun download(inputUrl: String, outputFile: String) {
        // Downloads a file and updates bytes read
        // Calls setForegroundInfo() periodically when it needs to update
        // the ongoing Notification
        var count = 0
        val file = File(
            getRootFile(applicationContext).path,
            outputFile
        )

        val url = URL(inputUrl)
        val connnection = url.openConnection()
        connnection.connect()

        var fileLength = connnection.contentLength

        var input = BufferedInputStream(url.openStream())
        var output = FileOutputStream(file)
        val data = ByteArray(1024)
        var total = 0
        var progress = 0
        setProgress(workDataOf(KEY_DOWNLOAD_WORK_PROGRESS to progress))
        while (input.read(data).also { count = it } != -1) {

            total += count
            var p = (total.toDouble() / fileLength) * 100
            val diffProgress = p.toInt() - progress
            progress = p.toInt()
            setProgress(workDataOf(KEY_DOWNLOAD_WORK_PROGRESS to progress))
            if (diffProgress >= 1)
                setForeground(createForegroundInfo((inputUrl + outputFile).hashCode(), progress))
            Log.d("progress", p.toString())
            output.write(data, 0, count)

        }


        output.flush()
        output.close()
        input.close()

        setProgress(workDataOf(KEY_DOWNLOAD_WORK_PROGRESS to 100))

        setForeground(createForegroundInfo((inputUrl + outputFile).hashCode(), 100))
    }

    // Creates an instance of ForegroundInfo which can be used to update the
    // ongoing notification.
    private fun createForegroundInfo(notificationId: Int, progress: Int): ForegroundInfo {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val title = applicationContext.getString(R.string.notification_title)
        val cancel = applicationContext.getString(R.string.cancel_download)
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setProgress(100, progress, false)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(notificationId, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val name = applicationContext.getString(R.string.notification_channel_name)
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(id, name, importance)
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    companion object {
        const val KEY_INPUT_URL = "KEY_INPUT_URL"
        const val KEY_OUTPUT_FILE_NAME = "KEY_OUTPUT_FILE_NAME"
        const val KEY_DOWNLOAD_WORK_PROGRESS = "KEY_DOWNLOAD_WORK_PROGRESS"
    }
}