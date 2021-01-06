package app.vtcnews.testvlc.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.vtcnews.testvlc.model.Playlist
import app.vtcnews.testvlc.network.PlaylistService
import app.vtcnews.testvlc.repo.PlaylistRepo
import app.vtcnews.testvlc.utils.getFileSize
import app.vtcnews.testvlc.utils.isFileDownloaded
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import kotlinx.coroutines.launch
import java.io.File


class MainViewmodel @ViewModelInject constructor(
    private val playlistRepo: PlaylistRepo,
    private val playlistService: PlaylistService
) : ViewModel() {

    val playlist = MutableLiveData<List<Playlist>>()

    var playlistIndex = 1

    fun getPlaylist(context: Context) {
        viewModelScope.launch {
            val body = mapOf(
                "IMEI" to "8A:65:48:62:36:1D"
            )

            val res = playlistService.getPlaylist(body)
            if(res.isSuccessful)
                playlist.value = res.body()!!.playlists
        }
    }


    fun download(appContext: Context) {
        val storagePath = appContext.filesDir.path
        if (!playlist.value.isNullOrEmpty()) {
            val listVideo = playlist.value!!
            listVideo.forEach {
                val url = it.path
                if (!url.isNullOrEmpty() && url.startsWith("http")) {
                    val f = File(Uri.parse(url).path).name

                    Log.d("fileName", f)

                    val fileName = "${it.id}${it.index}.mp3"

                    if (!isFileDownloaded(storagePath, fileName)) {
                        PRDownloader.download(url, storagePath, fileName).build()
                            .start(object : OnDownloadListener {
                                override fun onDownloadComplete() {
                                    it.path = "$storagePath/$fileName"
                                    Log.d("downloadcomplete", it.path!!)
                                }

                                override fun onError(error: Error?) {

                                }
                            })
                    } else {
                        it.path = "$storagePath/$fileName"
                        Log.d(
                            "file-video",
                            "download: $fileName - ${getFileSize(storagePath, fileName)}"
                        )
                    }
                }
            }
        }
    }
}