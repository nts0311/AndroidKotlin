package app.vtcnews.testvlc.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Request(@Json(name = "IMEI") val IMEI: String)