package com.axtv.app

enum class EntryType { FOLDER, VIDEO, FILE }

data class RemoteEntry(
    val name: String,
    val url: String,
    val type: EntryType
)
