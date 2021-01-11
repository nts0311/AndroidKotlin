package app.vtcnews.testvlc.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class MediaItem(
    @Json(name = "Index")
    var index: String? = null,

    @Json(name = "Name")
    var name: String? = null,

    @Json(name = "ID")
    var id: Long = 0,

    @Json(name = "Duration")
    var duration: String? = null,

    @Json(name = "Path")
    var path: String = "",

    @Json(name = "Start")
    var start: String? = null,

    @Json(name = "End")
    var end: String? = null,

    @Json(name = "Edit")
    var edit: Boolean = false,

    @Json(name = "Category")
    var category: Category? = null,

    var isCheck: Boolean = false,

    var fixTime: String = ""
) : Serializable {

    var pathBackup: String = ""
        set(value) {
            if (value.startsWith("http"))
                field = value
        }

    init {
        pathBackup = path
    }
}

@JsonClass(generateAdapter = true)
data class Category(

    @Json(name = "ID")
    var id: Long = 0,

    @Json(name = "Name")
    var name: String? = null
)


class ResponseList {
    @Json(name = "Playlist")
    var playlists: List<MediaItem>? = null
}
