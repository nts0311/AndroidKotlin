package app.vtcnews.testvlc

import android.util.Log
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.MediaPlayer

class CustomPlayer(libVlc: LibVLC) : MediaPlayer(libVlc) {
    var eventListener: (Int) -> Unit = {}

    override fun onEventNative(eventType: Int, arg1: Long, arg2: Long, argf1: Float): Event {
        val res = super.onEventNative(eventType, arg1, arg2, argf1)

        eventListener.invoke(eventType)

        Log.d("error", eventType.toString())


        return res
    }

}