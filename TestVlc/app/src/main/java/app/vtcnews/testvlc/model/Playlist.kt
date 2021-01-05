package app.vtcnews.testvlc.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import java.io.Serializable
import java.util.*

class Playlist : Serializable {
    @Json(name = "Index")
    
     var index: String? = null

    @Json(name = "Name")
    
     var name: String? = null

    @Json(name = "ID")
    
     var id: Long? = null

    @Json(name = "Duration")
    
     var duration: String? = null

    @Json(name = "Path")
    
     var path: String? = null

    @Json(name = "Start")
    
     var start: String? = null

    @Json(name = "End")
    
     var end: String? = null

    @Json(name = "Edit")
    
     var edit = false

    @Json(name = "Category")
    
     var category: Category? = null
     var isCheck = false
}

class Category : Serializable {
    @Json(name = "ID")
    
     var id: Long? = null

    @Json(name = "Name")
    
     var name: String? = null
}
class ResponseList {
    @Json(name = "Playlist")
    var playlists: List<Playlist>? = null
}
