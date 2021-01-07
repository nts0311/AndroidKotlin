package app.vtcnews.testvlc.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Paths
import kotlin.jvm.Throws

const val PLAYLIST_FILE_NAME = "playlist.json"

fun isFileExsisted(storagePath: String, fileName: String): Boolean {
    val file = File(storagePath, fileName)
    return file.exists()
}

fun getFileSize(storagePath: String, fileName: String): Long {
    val file = File(storagePath, fileName)
    return file.length()
}

@Throws(IOException::class)
suspend fun readFile(filePath: String) : String  {
    return withContext(Dispatchers.IO)
    {
        val br = BufferedReader(FileReader(filePath))

        val res =  br.lineSequence().toList().joinToString(separator = "")
        br.close()
        res
    }
}

@Throws(IOException::class)
suspend fun writeFile(filePath: String, content: String)   {
    withContext(Dispatchers.IO)
    {
        val bw = BufferedWriter(FileWriter(filePath))
        bw.write(content)
        bw.close()
    }
}