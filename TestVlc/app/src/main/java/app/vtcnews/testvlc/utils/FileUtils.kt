package app.vtcnews.testvlc.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

const val PLAYLIST_FILE_NAME = "playlist.json"

fun isFileExisted(storagePath: String, fileName: String): Boolean {
    val file = File(storagePath, fileName)
    return file.exists() && file.isFile
}


@Throws(IOException::class)
suspend fun readFile(filePath: String) : String {
    return withContext(Dispatchers.IO)
    {
        val br = BufferedReader(FileReader(filePath))

        val res = br.lineSequence().toList().joinToString(separator = "")
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