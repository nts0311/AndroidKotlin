package app.vtcnews.testvlc.repo

import android.content.Context
import android.util.Log
import app.vtcnews.testvlc.model.Playlist
import app.vtcnews.testvlc.network.PlaylistService
import app.vtcnews.testvlc.utils.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepo @Inject constructor(
    private val playlistService: PlaylistService,
    private val moshi: Moshi,
    @ApplicationContext private val appContext: Context
) {
    var playlist = listOf<Playlist>()

    private val type = Types.newParameterizedType(List::class.java, Playlist::class.java)
    private val jsonAdapter: JsonAdapter<List<Playlist>> = moshi.adapter(type)

    var body = mapOf(
        "IMEI" to Utils.getDeviceId(appContext)!!
    )

    suspend fun getPlaylist(storagePath: String, needUpdate: Boolean): List<Playlist> {

        return if (needUpdate) {
            updatePlaylist(storagePath)
        } else {
            val result = if (isFileExisted(storagePath, PLAYLIST_FILE_NAME)) {
                Log.d("readplaylist", "local")
                readPlaylistFromFile(storagePath)
            } else {
                updatePlaylist(storagePath)
            }
            result
        }
    }

    suspend fun updatePlaylist(storagePath: String): List<Playlist> {
        var result = listOf<Playlist>()

        try {
            val res = playlistService.getPlaylist(body)
            if (res.isSuccessful) {
                val resBody = res.body()
                if (resBody != null && resBody.playlists != null) {
                    result = resBody.playlists!!
                    playlist = result
                    savePlaylistToFile(result, storagePath)
                }
            }
        } catch (e: Exception) {
            Log.d("yee", e.toString())
        }

        return result
    }

    suspend fun savePlaylistToFile(playlist: List<Playlist>, storagePath: String) {
        withContext(Dispatchers.IO)
        {


            val filePath = "$storagePath/$PLAYLIST_FILE_NAME"
            val json = jsonAdapter.toJson(playlist)
            Log.d("writefile", json)

            try {
                writeFile(filePath, json)
            } catch (e: IOException) {
                Log.d(LOG_TAG, "error writing playlist!!!")
            }
        }
    }

    private suspend fun readPlaylistFromFile(storagePath: String): List<Playlist> {

        var result = listOf<Playlist>()

        withContext(Dispatchers.IO)
        {
            val filePath = "$storagePath/$PLAYLIST_FILE_NAME"

            try {
                var contentFromFile = readFile(filePath)
                result = jsonAdapter.fromJson(contentFromFile) ?: listOf()
            } catch (e: IOException) {
                Log.d(LOG_TAG, "error reading playlist!!!")
            }

        }
        return result
    }

    companion object {
        private const val LOG_TAG = "PlaylistRepo"
    }
}