package app.vtcnews.testvlc.viewmodels

import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.vtcnews.testvlc.model.Playlist
import app.vtcnews.testvlc.network.PlaylistService
import app.vtcnews.testvlc.utils.Utils
import com.downloader.*
import kotlinx.coroutines.launch


class MainViewmodel @ViewModelInject constructor(
    private val playlistService: PlaylistService
) : ViewModel() {

    val playlist = MutableLiveData<List<Playlist>>()

    var playlistIndex = 0

    fun getPlaylist(context: Context)
    {
        viewModelScope.launch {
            val body = mapOf(
                "IMEI" to "8A:65:48:62:36:1D"
            )

            val res = playlistService.getPlaylist(body)
            playlist.value = res.playlists
        }
    }

    fun download(appContext : Context) {
        val storagePath = "/data/data/app.vtcnews.testvlc"//Utils.getRootDirPath(appContext)
        if (!playlist.value.isNullOrEmpty()) {
            val listVideo = playlist.value!!
            listVideo.forEach {
                val url = it.path
                if(!url.isNullOrEmpty() && url.startsWith("http"))
                {
                    val fileName = "${it.id}${it.index}.mp4"

                    PRDownloader.download(url, storagePath, fileName).build()
                        .start(object : OnDownloadListener
                        {
                            override fun onDownloadComplete() {
                                it.path = "$storagePath/$fileName"
                                Log.d("downloadcomplete", it.path!!)
                            }

                            override fun onError(error: Error?) {

                            }
                        })
                }
            }
        }
    }
}