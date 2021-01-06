package app.vtcnews.testvlc

import android.net.Uri
import android.os.Bundle
import app.vtcnews.testvlc.model.Video
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import app.vtcnews.testvlc.model.PingHubRequest
import app.vtcnews.testvlc.model.ReponseHub
import app.vtcnews.testvlc.utils.NetworkUtils
import app.vtcnews.testvlc.utils.Status
import app.vtcnews.testvlc.utils.Utils
import app.vtcnews.testvlc.viewmodels.MainViewmodel
import com.google.gson.Gson
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.squareup.moshi.Moshi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.util.VLCVideoLayout
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


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

    @Inject
    lateinit var moshi : Moshi

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

        viewModel.playlist.observe(this)
        {
            viewModel.download(applicationContext)
            val videoPath = it[viewModel.playlistIndex].path!!
            val media = if(videoPath.startsWith("http"))
            {
                Log.d("link", "online: $videoPath")
                Media(mLibVLC, Uri.parse(videoPath))
            }

            else
            {
                Log.d("link", "local: $videoPath")
                Media(mLibVLC, Uri.parse("file://$videoPath"))
            }
            media.addOption(":fullscreen")
            mMediaPlayer!!.play(media)
        }

    }

    fun nextVideo()
    {
        mVideoLayout!!.post {
            val playlist = viewModel.playlist.value!!
            viewModel.playlistIndex++
            if (viewModel.playlistIndex >= playlist.size)
                viewModel.playlistIndex = 0

            val videoPath = playlist[viewModel.playlistIndex].path!!
            val media = if(videoPath.startsWith("http"))
            {
                Log.d("link", "online: $videoPath")
                Media(mLibVLC, Uri.parse(videoPath))
            }

            else
            {
                Log.d("link", "local: $videoPath")
                Media(mLibVLC, Uri.parse("file://$videoPath"))
            }

            media.addOption(":fullscreen")

            mMediaPlayer!!.media = media
            mMediaPlayer!!.play()

            //media.addSlave(Media.Slave())
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
                            pingTimer()
                        }
                    }.execute(hubConnection)
                    onMessage()
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

    private fun onMessage() {
        hubConnection!!.on(
            "ReceiveMessage",
            { message: String?, message1: String? ->
                runOnUiThread {
                    Log.d("command", message!!)
                    Log.d("message", message1!!)
                    handleFromCommandServer(message, message1)
                }
            },
            String::class.java,
            String::class.java
        )
    }

    // ham gui du lieu
    private fun sendMessage(mess: String, command: String) {
        try {
            hubConnection!!.invoke(command, "8A:65:48:62:36:1D", mess)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playURLVideo(index: Int)
    {
        viewModel.playlistIndex = index
        val videoPath = viewModel.playlist.value!![viewModel.playlistIndex].path!!
        val media = if(videoPath.startsWith("http"))
        {
            Log.d("link", "online: $videoPath")
            Media(mLibVLC, Uri.parse(videoPath))
        }

        else
        {
            Log.d("link", "local: $videoPath")
            Media(mLibVLC, Uri.parse("file://$videoPath"))
        }
        media.addOption(":fullscreen")
        mMediaPlayer!!.play(media)
    }

    private fun handleFromCommandServer(commamd: String?, message: String?) {
        try {
            if (commamd == null || commamd.isEmpty()) {
                return
            }
            if (message == null || message.isEmpty()) {
                return
            }
            //count_ping_hub = 0
            var reponseHub = ReponseHub()
            if (message.startsWith("{")) {
                //reponseHub = gson.fromJson(message, ReponseHub::class.java)

                val jsonAdapter = moshi.adapter(ReponseHub::class.java)
                reponseHub = jsonAdapter.fromJson(message)!!
            }
            val isImei = "8A:65:48:62:36:1D" == reponseHub.imei
            if (isImei) {
                //deviceAdrress = ""
                when (commamd) {
                    Status.GET_LIST -> {
                    }
                    Status.UPDATE_LIST -> {
                    }
                    Status.NEXT -> {
                    }
                    Status.PREVIEW -> {
                    }
                    Status.JUMP -> {
                        Log.d(commamd, message)
                        val id = reponseHub.message!!.trim().toInt()
                        val volume = reponseHub.volume
                        playURLVideo(id)
                    }
                    Status.LIVE -> {
                        /*volume = reponseHub.getVolume()
                        playURLVideo(reponseHub.getMessage().trim(), false)*/
                    }
                    //Status.UPDATE_STATUS -> pingHub(true)
                    Status.GET_LOCATION -> {
                    }
                    Status.SET_VOLUME -> {
                    }
                    Status.STOP -> {
                    }
                    Status.PAUSE -> {
                    }
                    Status.START -> {
                    }
                    Status.RESTART -> {
                    }
                    Status.RELOAD -> viewModel.getPlaylist(applicationContext)
                    Status.SWITCH_MODE_FM -> {
                    }
                    Status.SET_MUTE_DEVICE -> {
                    }
                    Status.SET_VOLUME_DEVICE -> {
                    }
                    Status.GET_VOLUME_DEVICE -> {
                    }
                    Status.GET_SOURCE_AUDIO -> {
                    }
                    /*Status.GET_PA -> {
                        deviceAdrress = Define.Power_Amplifier_R
                        writeToDevice(buildReadMessageNew(Define.FUNC_WRITE_READ_DEVICE_INFO, "6"))
                    }*/
                    Status.GET_FM_FQ -> {
                    }
                    Status.GET_AM_FQ -> {
                    }
                    Status.GET_TEMPERATURE -> {
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun pingHub(isUpdate: Boolean) {
        try {
            /*if (!isUpdate) {
                if (count_ping_hub > 1) {
                    if (hubConnection == null) {
                        connectHub()
                    } else {
                        hubConnection!!.start()
                    }
                }
            }*/
            if (!this@MainActivity.isFinishing) {
                // send ping_hub || update_status
                //  val date: String = simpleDateFormat.format(Date())
                    Log.d("test", "ping hub")
                var request: PingHubRequest? = null
                var video: Video? = null
                val i = viewModel.playlistIndex
                Log.d("player:", java.lang.String.valueOf(i))
                //if (i >= 0 && i < mainViewModel.lstLiveData.getValue().size()) {
                video = Video("" + i, "-1")
                /*request = PingHubRequest.builder().connectionId(hubConnection!!.connectionId)
                    .imei(Utils.getDeviceId(this)).status(if (isPlaying()) "START" else "STOP")
                    .startTine(date)
                    .video(Gson().toJson(video))
                    .build()
                //}*/

                val videoAdater = moshi.adapter(Video::class.java)

                request = PingHubRequest().apply {
                    imei = "8A:65:48:62:36:1D"
                    status = "START"
                    connectionId = (hubConnection!!.connectionId)
                    this.video = videoAdater.toJson(video)

                    val dateformat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")



                    startTine = dateformat.format(Date())
                }


                //count_ping_hub = count_ping_hub + 1
                val requestAdater = moshi.adapter(PingHubRequest::class.java)
                sendMessage(requestAdater.toJson(request), Utils.ping)
                Log.d("request", requestAdater.toJson(request))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun pingTimer()
    {
        lifecycleScope.launchWhenResumed {
            while (true)
            {
                pingHub(true)
                Log.d("pingtimer","ping")
                delay(15000)
            }
        }
    }
}