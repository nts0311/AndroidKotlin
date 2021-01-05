package app.vtcnews.testvlc

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.vtcnews.testvlc.utils.NetworkUtils
import app.vtcnews.testvlc.viewmodels.MainViewmodel
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import dagger.hilt.android.AndroidEntryPoint
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.util.VLCVideoLayout
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val USE_TEXTURE_VIEW = false
    private val ENABLE_SUBTITLES = true
    private val ASSET_FILENAME = "bbb.m4v"

    private var mVideoLayout: VLCVideoLayout? = null

    private var mLibVLC: LibVLC? = null
    private var mMediaPlayer: CustomPlayer? = null

    val viewModel by viewModels<MainViewmodel>()

    val images = listOf<String>(
        "https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png",
        "https://www.talkwalker.com/images/2020/blog-headers/image-analysis.png",
        "https://www.fnordware.com/superpng/pnggradHDrgba.png"
    )

    private var hubConnection: HubConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val args = ArrayList<String>()
        args.add("-vvv")
        args.add("--aout=opensles")
        args.add("--avcodec-codec=h264")
        args.add("--network-caching=2000")
        args.add("--no-http-reconnect")
        args.add("--file-logging")
        args.add("--logfile=vlc-log.txt")


        mLibVLC = LibVLC(this, args)
        mMediaPlayer = CustomPlayer(mLibVLC!!)
        mVideoLayout = findViewById(R.id.video_layout)

        NetworkUtils.getInstance().startNetworkListener(this)

        test()
        //hideSystemUi()

        viewModel.playlist.observe(this)
        {
            val media = Media(mLibVLC, Uri.parse(it[viewModel.playlistIndex].path))
            media.addOption(":fullscreen")
            mMediaPlayer!!.play(media)

            //mMediaPlayer!!.play()
        }

    }

    fun nextVideo()
    {
        mVideoLayout!!.post {
            val playlist = viewModel.playlist.value!!
            viewModel.playlistIndex++
            if (viewModel.playlistIndex >= playlist.size)
                viewModel.playlistIndex = 0

            val media = Media(mLibVLC, Uri.parse(playlist[viewModel.playlistIndex].path))
            media.addOption(":fullscreen")

            mMediaPlayer!!.media = media
            mMediaPlayer!!.play()

        }
    }

    private fun hideSystemUi() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())

        } else {
            // hide status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer!!.release()
        mLibVLC!!.release()
    }

    override fun onStart() {
        super.onStart()
        var i = 0

        /*lifecycleScope.launchWhenResumed {
            while (true) {
                if (i > 2) i = 0

                val media = Media(mLibVLC, Uri.parse(images[i]))
                i++
                mMediaPlayer!!.media = media
                mMediaPlayer!!.play()
                mMediaPlayer!!.setEventListener {

                }
                delay(5000)
            }
        }*/

        mMediaPlayer!!.attachViews(mVideoLayout!!, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW)

        mMediaPlayer!!.endListener = {
            nextVideo()
        }

    }

    private fun test() {
        NetworkUtils.getInstance().mNetworkLive.observe(this)
        { isConnected ->
            if (isConnected) {
                if (hubConnection == null) {
                    hubConnection = HubConnectionBuilder.create(NetworkUtils.URL_HUB).build()

                    Log.d("Connected", "Connected")

                    /*mainViewModel = AndroidViewModelFactory(application).create(MainViewModel::class.java)
                    if (mainViewModel.lstLiveData == null || mainViewModel.lstLiveData.getValue() == null || mainViewModel.lstLiveData.getValue()
                            .size() === 0
                    ) {
                        Log.d("vaoday111", "vaoday111")
                        //mainViewModel.getPlayList(this) { playlists -> createRecyclerView(playlists) }
                    }*/
                    HubConnectionTask { connectionId ->
                        if (connectionId != null) {
                            Log.d("Connected", connectionId)
                            viewModel.getPlaylist(this@MainActivity)
                        }
                    }.execute(hubConnection)
                }
            } else
                hubConnection = null
        }
    }

    override fun onStop() {
        super.onStop()
        mMediaPlayer!!.stop()
        mMediaPlayer!!.detachViews()
    }
}