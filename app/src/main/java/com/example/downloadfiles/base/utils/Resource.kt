package com.example.downloadfiles.base.utils

import java.io.IOException

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class ApiError(val code: Int, val error: String?) : Resource<Nothing>()
    data class NetworkError(val exception: IOException) : Resource<Nothing>()
    data class UnknownError(val exception: Throwable?) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}