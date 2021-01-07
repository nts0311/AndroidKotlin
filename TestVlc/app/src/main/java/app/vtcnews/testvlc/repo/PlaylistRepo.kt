package app.vtcnews.testvlc.repo

import android.util.Log
import app.vtcnews.testvlc.model.Playlist
import app.vtcnews.testvlc.network.PlaylistService
import app.vtcnews.testvlc.utils.PLAYLIST_FILE_NAME
import app.vtcnews.testvlc.utils.isFileExsisted
import app.vtcnews.testvlc.utils.readFile
import app.vtcnews.testvlc.utils.writeFile
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepo @Inject constructor(
    private val playlistService: PlaylistService,
    private val moshi: Moshi
) {
    var playlist = listOf<Playlist>()

    private val type = Types.newParameterizedType(List::class.java, Playlist::class.java)
    private val jsonAdapter: JsonAdapter<List<Playlist>> = moshi.adapter(type)

    var body = mapOf(
        "IMEI" to "8A:65:48:62:36:1D"
    )

    suspend fun getPlaylist(storagePath: String): List<Playlist> {
        var result = listOf<Playlist>()
        if (isFileExsisted(storagePath, PLAYLIST_FILE_NAME)) {
            Log.d("readplaylist","local")
            result = readPlaylistFromFile(storagePath)
        } else {
            Log.d("readplaylist","online")
            val res = playlistService.getPlaylist(body)
            if (res.isSuccessful) {
                val resBody = res.body()
                if (resBody != null && resBody.playlists != null) {
                    result = resBody.playlists!!
                    playlist = result
                    savePlaylistToFile(result, storagePath)
                }
            }
        }

        return result
    }

    suspend fun savePlaylistToFile(playlist: List<Playlist>, storagePath: String) {
        withContext(Dispatchers.IO)
        {
            val filePath = "$storagePath/$PLAYLIST_FILE_NAME"
            val json = jsonAdapter.toJson(playlist)
            Log.d("writefile", json)
            writeFile(filePath, json)
        }
    }

    private suspend fun readPlaylistFromFile(storagePath: String): List<Playlist> {

        var result = listOf<Playlist>()

        withContext(Dispatchers.IO)
        {
            val filePath = "$storagePath/$PLAYLIST_FILE_NAME"
            var contentFromFile = readFile(filePath)
            result = jsonAdapter.fromJson(contentFromFile) ?: listOf()
        }
        return result
    }
}