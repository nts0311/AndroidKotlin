package app.vtcnews.testvlc

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import app.vtcnews.testvlc.model.*
import app.vtcnews.testvlc.utils.NetworkUtils
import app.vtcnews.testvlc.utils.Status
import app.vtcnews.testvlc.utils.Utils
import app.vtcnews.testvlc.utils.downloadUpdateApk
import app.vtcnews.testvlc.viewmodels.MainViewmodel
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.squareup.moshi.Moshi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import org.videolan.libvlc.*
import org.videolan.libvlc.util.VLCVideoLayout
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val USE_TEXTURE_VIEW = false
    private val ENABLE_SUBTITLES = true

    private val UPDATE_PERMISSION_REQUEST_CODE = 1

    private var mVideoLayout: VLCVideoLayout? = null

    private lateinit var mLibVLC: LibVLC
    private lateinit var visualPlayer: CustomPlayer
    private lateinit var audioPlayer: CustomPlayer
    var mainPlayer: CustomPlayer? = null

    val viewModel by viewModels<MainViewmodel>()

    private var hubConnection: HubConnection? = null

    lateinit var btnUpdate: Button

    @Inject
    lateinit var moshi: Moshi

    private var presentImageJob: Job? = null
    var checkScheduledMediaJob: Job? = null


    private lateinit var playlist: Playlist

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
        visualPlayer = CustomPlayer(mLibVLC)
        audioPlayer = CustomPlayer(mLibVLC)
        mVideoLayout = findViewById(R.id.video_layout)

        NetworkUtils.getInstance().startNetworkListener(this)

        NetworkUtils.getInstance().mNetworkLive.observe(this)
        { isConnected ->
            if (isConnected)
                connectToHub()
        }

        btnUpdate = findViewById(R.id.btn_update)
        btnUpdate.setOnClickListener {
            if (needGrantPermission()) {
                requestUpdatePermission()
            } else
                downloadUpdateApk("https://mam.tek4tv.vn/download/player_203.apk", this)
        }

        playlist = Playlist(lifecycleScope)
        playlist.playMediaByIndex = {
            playMediaByIndex(it)
        }


        connectToHub()
        startPlayingMedia()
        registerObservers()
    }

    override fun onStop() {
        super.onStop()
        mainPlayer?.stop()
        audioPlayer.stop()
        visualPlayer.stop()
        visualPlayer.detachViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainPlayer?.release()
        if (!audioPlayer.isReleased)
            audioPlayer.release()

        if (!visualPlayer.isReleased)
            visualPlayer.release()

        mLibVLC.release()
    }

    override fun onStart() {
        super.onStart()
        visualPlayer.attachViews(mVideoLayout!!, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            UPDATE_PERMISSION_REQUEST_CODE -> {


                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("quyen", grantResults.toList().toString())
                    downloadUpdateApk("https://mam.tek4tv.vn/download/player_203.apk", this)

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(
                        this@MainActivity,
                        "Permission denied to read your External storage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    private fun needGrantPermission(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.REQUEST_INSTALL_PACKAGES
        )

        var needGrantPermission = false

        permissions.forEach {
            if (checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
                needGrantPermission = true
                return@forEach
            }
        }

        return needGrantPermission
    }

    private fun requestUpdatePermission() {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.REQUEST_INSTALL_PACKAGES,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        requestPermissions(permissions, UPDATE_PERMISSION_REQUEST_CODE)
    }


    private fun registerObservers() {
        viewModel.playlist.observe(this)
        {
            playlist.mediaItems = it


            it[3].fixTime = "10:44:00"

            checkScheduledMedia()

            viewModel.downloadMedias(applicationContext)

            if (viewModel.playlistIndex >= viewModel.playlist.value!!.size)
                viewModel.playlistIndex = 0
            playMediaByIndex(viewModel.playlistIndex)
        }
    }

    private fun startPlayingMedia() {
        viewModel.getPlaylist(applicationContext, false)
        viewModel.checkPlaylist(applicationContext)
    }


    private fun playMediaByIndex(index: Int) {
        val playlist = viewModel.playlist.value

        if (playlist == null || playlist.isEmpty()) return

        val mediaItem = viewModel.playlist.value!![index]
        val filePath = mediaItem.path!!

        val mediaName = if (filePath.startsWith("http")) File(Uri.parse(filePath).path).name
        else File("file://$filePath").name


        val media1 =
            if (filePath.isNotEmpty() && File(filePath).exists()) {
                Log.d("link", "local: $filePath")
                Media(mLibVLC, Uri.parse("file://$filePath"))
            } else {
                Log.d("link", "online: $filePath")
                Media(mLibVLC, Uri.parse(viewModel.playlist.value!![index].pathBackup))
            }

        val audioExt = listOf(".mp3")
        val videoExt = listOf(".mp4")

        val isAudio = audioExt.any { mediaName.endsWith(it) }
        val isVideo = videoExt.any { mediaName.endsWith(it) }


        presentImageJob?.cancel()
        if (isAudio) {
            mainPlayer = audioPlayer
            audioPlayer.play(media1)
            presentImage(mediaItem.duration!!)
        } else if (isVideo) {
            audioPlayer.stop()
            media1.addOption(":fullscreen")
            mainPlayer = visualPlayer
            visualPlayer.play(media1)
        }

        mainPlayer!!.eventListener = { event ->
            when (event) {
                MediaPlayer.Event.EndReached -> {
                    playNextMedia()
                    //remove callback
                    mainPlayer!!.eventListener = {}
                }

                MediaPlayer.Event.EncounteredError -> {
                    presentImageJob?.cancel()
                    presentImageJob = null

                    playNextMedia()
                    //remove callback
                    mainPlayer!!.eventListener = {}
                }
                MediaPlayer.Event.Stopped -> viewModel.isPlaying = false
                MediaPlayer.Event.Playing -> viewModel.isPlaying = true
            }
        }
    }

    private fun presentImage(duration: String) {

        val imageList = listOf(
            Uri.parse("https://picsum.photos/600/600"),
            Uri.parse("https://picsum.photos/600/600"),
            Uri.parse("https://picsum.photos/600/600"),
            Uri.parse("https://picsum.photos/600/600"),
            Uri.parse("https://picsum.photos/600/600"),
            Uri.parse("https://picsum.photos/600/600"),
            Uri.parse("https://picsum.photos/600/600"),
            Uri.parse("https://picsum.photos/600/600"),
            Uri.parse("https://picsum.photos/600/600"),
            Uri.parse("https://picsum.photos/600/600")
        )

        /*val imageList = listOf(
            Uri.parse("file:///data/data/app.vtcnews.testvlc/files/1.jpg"),
            Uri.parse("file:///data/data/app.vtcnews.testvlc/files/2.jpg"),
            Uri.parse("file:///data/data/app.vtcnews.testvlc/files/3.jpg"),
            Uri.parse("file:///data/data/app.vtcnews.testvlc/files/4.jpg"),
            Uri.parse("file:///data/data/app.vtcnews.testvlc/files/5.jpg"),
            Uri.parse("file:///data/data/app.vtcnews.testvlc/files/6.jpg"),
            Uri.parse("file:///data/data/app.vtcnews.testvlc/files/7.jpg"),
            Uri.parse("file:///data/data/app.vtcnews.testvlc/files/8.jpg"),
            Uri.parse("file:///data/data/app.vtcnews.testvlc/files/9.jpg"),
            Uri.parse("file:///data/data/app.vtcnews.testvlc/files/11.jpg"),
        )*/

        val imageDuration = try {
            (duration.split(":").mapIndexed { index, s ->
                when (index) {
                    0 -> s.toInt() * 3600
                    1 -> s.toInt() * 60
                    2 -> s.toInt()
                    else -> 0
                }
            }
                .fold(0L) { acc, it -> acc + it } * 1000) / imageList.size
        } catch (e: Exception) {
            5000
        }

        presentImageJob = lifecycleScope.launch {
            var i = 0
            while (i < imageList.size && isActive) {
                Log.d("presentimage", imageList[i].path!! + this.coroutineContext.toString())
                val media = Media(mLibVLC, imageList[i++])
                visualPlayer.play(media)
                delay(imageDuration)
            }
        }
    }


    private fun playNextMedia() {
        mVideoLayout!!.post {
            val playlist = viewModel.playlist.value!!
            viewModel.playlistIndex++
            if (viewModel.playlistIndex >= playlist.size)
                viewModel.playlistIndex = 0

            playMediaByIndex(viewModel.playlistIndex)
        }
    }

    fun checkScheduledMedia() {
        checkScheduledMediaJob?.cancel()
        checkScheduledMediaJob = lifecycleScope.launch(Dispatchers.Default) {

            val playlist = viewModel.playlist.value ?: listOf()
            val scheduledItems: MutableList<MediaItem> = mutableListOf()
            scheduledItems.addAll(playlist.filter { it.fixTime.isNotEmpty() })

            while (true) {

                Log.d("Playlist", "check scheduled")
                scheduledItems.forEachIndexed { index, mediaItem ->
                    if (mediaItem.fixTime.isNotEmpty()) {
                        try {
                            val time = mediaItem.fixTime.split(":").map { it.toInt() }

                            val now = Calendar.getInstance()
                            val currentHour = now.get(Calendar.HOUR_OF_DAY)
                            val currentMin = now.get(Calendar.MINUTE)
                            val currentSecond = now.get(Calendar.SECOND)

                            if (time[0] <= currentHour
                                && time[1] <= currentMin
                                && time[2] <= currentSecond
                            ) {

                                withContext(Dispatchers.Main)
                                {
                                    val indexToPlay = playlist.indexOf(mediaItem)
                                    playMediaByIndex(indexToPlay)
                                    Log.d("Playlist", "play $index at $time")
                                    scheduledItems.removeAt(index)
                                    viewModel.playlistIndex = indexToPlay
                                }
                            }

                        } catch (e: NumberFormatException) {
                            Log.e("checkScheduledMedia", "error parsing media fixtime")
                        }
                    }
                }

                delay(1000)
            }
        }
    }

    private fun connectToHub() {
        NetworkUtils.getInstance().mNetworkLive.observe(this)
        { isConnected ->
            if (isConnected) {

                if (!viewModel.isPlaying)
                    startPlayingMedia()

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
                            pingTimer()
                        }
                    }.execute(hubConnection)
                    onMessage()
                }
            } else {
                hubConnection = null
            }

        }
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
            hubConnection!!.invoke(command, Utils.getDeviceId(applicationContext), mess)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playURLVideo(index: Int) {
        viewModel.playlistIndex = index
        playMediaByIndex(viewModel.playlistIndex)
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
                val jsonAdapter = moshi.adapter(ReponseHub::class.java)
                reponseHub = jsonAdapter.fromJson(message)!!
            }
            val isImei = Utils.getDeviceId(applicationContext) == reponseHub.imei
            if (isImei) {
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
                    Status.UPDATE_STATUS -> pingHub(true)
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
                    Status.RELOAD -> viewModel.getPlaylist(applicationContext, true)
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

                val i = viewModel.playlistIndex
                Log.d("player:", java.lang.String.valueOf(i))
                //if (i >= 0 && i < mainViewModel.lstLiveData.getValue().size()) {

                var mode = "-1"

                if (!viewModel.playlist.value.isNullOrEmpty()) {
                    val path = viewModel.playlist.value!![i].path!!
                    mode = if (path.isNotEmpty() && !File(path).exists()) "1"
                    else "0"
                }

                val video = Video("" + i, mode)

                val videoAdapter = moshi.adapter(Video::class.java)

                request = PingHubRequest().apply {
                    imei = Utils.getDeviceId(applicationContext)
                    status = "START"
                    connectionId = (hubConnection!!.connectionId)
                    this.video = videoAdapter.toJson(video)
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

    private fun pingTimer() {
        lifecycleScope.launchWhenResumed {
            while (true) {
                pingHub(true)
                Log.d("pingtimer", "ping")
                delay(15000)
            }
        }
    }


}