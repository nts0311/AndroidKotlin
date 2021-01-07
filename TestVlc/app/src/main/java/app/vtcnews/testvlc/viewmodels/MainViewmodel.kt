package app.vtcnews.testvlc.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.vtcnews.testvlc.model.Playlist
import app.vtcnews.testvlc.repo.PlaylistRepo
import app.vtcnews.testvlc.utils.isFileExisted
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


class MainViewmodel @ViewModelInject constructor(
    private val playlistRepo: PlaylistRepo
) : ViewModel() {

    val playlist = MutableLiveData<List<Playlist>>()

    private var saveFileJob: Job? = null

    var playlistIndex = 1

    fun getPlaylist(context: Context, needUpdate: Boolean) {
        viewModelScope.launch {
            val res = playlistRepo.getPlaylist(context.filesDir.path, needUpdate)
            playlist.value = res
        }
    }

    fun checkPlaylist(appContext: Context) {
        viewModelScope.launch {
            while (true) {
                if (playlist.value != null && playlist.value!!.isNotEmpty()) {
                    val needDownload = playlist.value!!.any {
                        it.path!!.startsWith("http")
                    }

                    if (needDownload)
                        downloadMedias(appContext)
                    Log.d("checkplaylist", needDownload.toString())
                }

                delay(30000)
            }
        }
    }

    fun downloadMedias(appContext: Context) {
        val storagePath = appContext.filesDir.path
        if (!playlist.value.isNullOrEmpty()) {
            val listVideo = playlist.value!!
            listVideo.forEach {
                //it.path = it.path!!.replace("\\","/").replace("\\\\","/")
                val url = it.path
                if (!url.isNullOrEmpty() && url.startsWith("http")) {
                    var fileName = File(Uri.parse(url).path).name

                    val slash = fileName.lastIndexOf("\\")

                    if (slash != -1) {
                        fileName = fileName.substring(slash + 1)
                    }

                    if (!isFileExisted(storagePath, fileName)) {
                        PRDownloader.download(url, storagePath, fileName).build()
                            .start(object : OnDownloadListener {
                                override fun onDownloadComplete() {
                                    it.path = "$storagePath/$fileName"

                                    viewModelScope.launch {
                                        saveFileJob?.join()
                                        saveFileJob = launch {
                                            playlistRepo.savePlaylistToFile(
                                                listVideo,
                                                storagePath
                                            )
                                        }
                                    }

                                    Log.d("downloadcomplete", it.path!!)
                                }

                                override fun onError(error: Error?) {

                                }


                            })
                    } else {
                        it.path = "$storagePath/$fileName"
                        viewModelScope.launch {
                            saveFileJob?.join()
                            saveFileJob = launch {
                                playlistRepo.savePlaylistToFile(
                                    listVideo,
                                    storagePath
                                )
                            }
                        }
                        /*it.path = "$storagePath/$fileName"
                        Log.d(
                            "file-video",
                            "download: $fileName - ${getFileSize(storagePath, fileName)}"
                        )*/
                    }
                }
            }
        }
    }


}