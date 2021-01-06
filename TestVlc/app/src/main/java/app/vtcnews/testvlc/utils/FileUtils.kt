package app.vtcnews.testvlc.utils

import java.io.File

const val PLAYLIST_FILE_NAME = "playlist.json"

fun isFileDownloaded(storagePath: String, fileName: String): Boolean {
    val file = File(storagePath, fileName)
    return file.exists()
}

fun getFileSize(storagePath: String, fileName: String): Long {
    val file = File(storagePath, fileName)
    return file.length()
}