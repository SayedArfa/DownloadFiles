package com.example.downloadfiles.base.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.example.downloadfiles.domain.model.entity.File

fun readAssetsFileAsString(context: Context, path: String): String {
    return context.assets.open(path).bufferedReader().use { it.readText() }
}

fun getFileNameFromFile(file: File): String {
    return file.id.toString() + file.url.substring(file.url.lastIndexOf('.'))
}

fun getRootFile(context: Context): java.io.File {
    val rootDir =
        java.io.File("${context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath}/downloads")
    if (!rootDir.exists()) {
        rootDir.mkdir()
    }
    return rootDir
}

fun isFileExist(file: File, context: Context): Boolean {
    val f = java.io.File(getRootFile(context), getFileNameFromFile(file))
    return f.exists()
}

fun deleteFileRecursive(path: String) {
    val fileOrDirectory = java.io.File(path)
    if (fileOrDirectory.exists()) {
        if (fileOrDirectory.isDirectory) {
            val files = fileOrDirectory.listFiles()
            files?.let {
                for (child in files)
                    deleteFileRecursive(child.absolutePath)
            }
        }
        fileOrDirectory.delete()
    }
}

fun openFile(context: Context, filePath: String) {
    try {
        Log.d("filePath",filePath)
        val file = java.io.File(filePath)

        val apkURI = FileProvider.getUriForFile(
            context,
            context.packageName + ".provider", file
        )
        val viewIntent = Intent()
        viewIntent.action = Intent.ACTION_VIEW
        viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION + Intent.FLAG_ACTIVITY_NEW_TASK)
        viewIntent.setDataAndType(apkURI, context.contentResolver.getType(apkURI))
//        viewIntent.putExtra(Intent.EXTRA_STREAM, apkURI)
        context.startActivity(Intent.createChooser(viewIntent, "View via"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}