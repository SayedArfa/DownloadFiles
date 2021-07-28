package com.example.downloadfiles.domain.model.entity

data class File(val id: Int, val name: String, val url: String, val type: String)
enum class FileType {
    VIDEO, PDF
}
