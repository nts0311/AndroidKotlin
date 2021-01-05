package app.vtcnews.testvlc.network

import app.vtcnews.testvlc.model.Playlist
import app.vtcnews.testvlc.model.ResponseList
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST



interface PlaylistService {
    @Headers("Content-Type: application/json")
    @POST("api/device/imei")
    suspend fun getPlaylist(@Body body: Map<String, String>) : ResponseList
}