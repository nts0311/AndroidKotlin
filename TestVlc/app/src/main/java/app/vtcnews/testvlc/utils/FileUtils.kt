package app.vtcnews.testvlc.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

const val PLAYLIST_FILE_NAME = "playlist.json"

fun isFileExisted(storagePath: String, fileName: String): Boolean {
    val file = File(storagePath, fileName)
    return file.exists() && file.isFile
}

fun getFileSize(storagePath: String, fileName: String): Long {
    val file = File(storagePath, fileName)
    return file.length()
}

@Throws(IOException::class)
suspend fun readFile(filePath: String) : String {

    return try {
        withContext(Dispatchers.IO)
        {
            val br = BufferedReader(FileReader(filePath))

            val res = br.lineSequence().toList().joinToString(separator = "")
            br.close()
            res
        }
    } catch (e: IOException) {
        Log.e("FileUtils", "error reading file: $filePath")
        ""
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