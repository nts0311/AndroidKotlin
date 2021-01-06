package app.vtcnews.testvlc

import android.util.Log
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer

class CustomPlayer(libVlc: LibVLC) : MediaPlayer(libVlc) {
    var endListener : () -> Unit = {}

    override fun onEventNative(eventType: Int, arg1: Long, arg2: Long, argf1: Float): Event {
        val res = super.onEventNative(eventType, arg1, arg2, argf1)

        if(eventType == Event.EndReached)
            endListener.invoke()


        return res
    }

}