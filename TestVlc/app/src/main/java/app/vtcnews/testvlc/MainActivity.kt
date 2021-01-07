package app.vtcnews.testvlc

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import app.vtcnews.testvlc.model.PingHubRequest
import app.vtcnews.testvlc.model.ReponseHub
import app.vtcnews.testvlc.model.Video
import app.vtcnews.testvlc.utils.NetworkUtils
import app.vtcnews.testvlc.utils.Status
import app.vtcnews.testvlc.utils.Utils
import app.vtcnews.testvlc.viewmodels.MainViewmodel
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.squareup.moshi.Moshi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.util.VLCVideoLayout
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val USE_TEXTURE_VIEW = false
    private val ENABLE_SUBTITLES = true

    private var mVideoLayout: VLCVideoLayout? = null

    private var mLibVLC: LibVLC? = null
    private var mMediaPlayer: CustomPlayer? = null

    val viewModel by viewModels<MainViewmodel>()

    private var hubConnection: HubConnection? = null

    lateinit var btnUpdate: Button

    @Inject
    lateinit var moshi: Moshi

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

        btnUpdate = findViewById(R.id.btn_update)
        btnUpdate.setOnClickListener {
            if (needGrantPermission()) {
                requestAppPermission()
            }
            else
                download("https://mam.tek4tv.vn/download/player_203.apk")
        }

        /*if(needGrantPermission())
            requestAppPermission()*/

        connectToHub()

        viewModel.getPlaylist(this@MainActivity)


        viewModel.playlist.observe(this)
        {
            viewModel.downloadMedias(applicationContext)

            if (viewModel.playlistIndex >= viewModel.playlist.value!!.size)
                viewModel.playlistIndex = 0
            playVideoByIndex(viewModel.playlistIndex)
        }

    }

    private fun needGrantPermission() : Boolean
    {
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

    private fun requestAppPermission()
    {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.REQUEST_INSTALL_PACKAGES,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        requestPermissions(permissions, 1)
    }

    private fun playVideoByIndex(index: Int) {
        val videoPath = viewModel.playlist.value!![index].path!!
        val media = if (videoPath.startsWith("http")) {
            Log.d("link", "online: $videoPath")
            Media(mLibVLC, Uri.parse(videoPath))
        } else {
            Log.d("link", "local: $videoPath")
            Media(mLibVLC, Uri.parse("file://$videoPath"))
        }
        media.addOption(":fullscreen")
        mMediaPlayer!!.play(media)
    }

    private fun nextVideo() {
        mVideoLayout!!.post {
            val playlist = viewModel.playlist.value!!
            viewModel.playlistIndex++
            if (viewModel.playlistIndex >= playlist.size)
                viewModel.playlistIndex = 0

            playVideoByIndex(viewModel.playlistIndex)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer!!.release()
        mLibVLC!!.release()
    }

    override fun onStart() {
        super.onStart()
        mMediaPlayer!!.attachViews(mVideoLayout!!, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW)

        mMediaPlayer!!.endListener = {
            nextVideo()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {


                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("quyen", grantResults.toList().toString())
                    download("https://mam.tek4tv.vn/download/player_203.apk")

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

    private fun connectToHub() {
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

    fun playURLVideo(index: Int) {
        viewModel.playlistIndex = index
        playVideoByIndex(viewModel.playlistIndex)
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

    fun pingTimer() {
        lifecycleScope.launchWhenResumed {
            while (true) {
                pingHub(true)
                Log.d("pingtimer", "ping")
                delay(15000)
            }
        }
    }

    private fun download(url: String) {
        var destination: String =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/"
        val fileName = "AppName.apk"
        destination += fileName
        val uri = Uri.parse("file://$destination")
        /*val uriInstall = FileProvider.getUriForFile(
            applicationContext, "$packageName.provider", File(
                destination
            )
        )*/
        //Uri.parse("file://$destination")

        //Delete update file if exists
        val file = File(destination)
        if (file.exists()) //file.delete() - test this, I think sometimes it doesnt work
            file.delete()
        //set downloadmanager
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDescription("update version")
        request.setTitle("Updating APK...")

        //set destination
        request.setDestinationUri(uri)

        // get download service and enqueue file
        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = manager.enqueue(request)

        //set BroadcastReceiver to install app when .apk is downloaded
        val finalDestination = destination
        Log.d("apk", finalDestination)


        val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context?, intent: Intent?) {
                //
                //
                // Install Updated APK
                try {
                    val builder = VmPolicy.Builder()
                    StrictMode.setVmPolicy(builder.build())

                    val install = Intent(Intent.ACTION_VIEW)
                    install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    install.setDataAndType(
                        uri,
                        manager.getMimeTypeForDownloadedFile(downloadId)
                    )
                    startActivity(install)

                    //                    if (proc.exitValue() == 0) {
//                        // Successfully installed updated app
//                        doRestart();
//                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun doRestart() {
        //writeToDevice(buildReadMessage(Define.FUNC_WRITE_RESTART_DEVICE, ""))
    }
}