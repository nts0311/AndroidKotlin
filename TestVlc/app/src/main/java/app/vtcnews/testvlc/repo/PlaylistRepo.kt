package app.vtcnews.testvlc.repo

import app.vtcnews.testvlc.model.Playlist
import app.vtcnews.testvlc.network.PlaylistService
import app.vtcnews.testvlc.utils.PLAYLIST_FILE_NAME
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepo @Inject constructor(
    private val playlistService: PlaylistService,
    private val moshi: Moshi
) {
    var playlist = listOf<Playlist>()

    /*suspend fun refreshPlaylist(header: Map<String, String>) {
        val res = playlistService.getPlaylist(header)

        if (!res.isSuccessful || res.body() == null) return

        val body = res.body()!!
        val playlist = body.playlists



       *//* val categoriesDb = mutableListOf<CategoryDb>()
        val playlistDb  = mutableListOf<PlaylistDb>()

        playlist?.forEach {
            if (it.category != null) {
                val dbItem = it.toDb()
                dbItem.categoryId = it.category?.id ?: -1L
                playlistDb.add(dbItem)

                categoriesDb.add(it.category!!.toDb())
            }
        }
        playlistDao.insertPlaylist(playlistDb)
        categoryDao.insertCategories(categoriesDb)*//*
    }*/

    private suspend fun savePlaylistToFile(playlist: Playlist, storagePath: String)
    {
        withContext(Dispatchers.IO)
        {
            val filePath = "$storagePath/$PLAYLIST_FILE_NAME"

            val jsonAdapter = moshi.adapter(Playlist::class.java)
        }
    }


    /*suspend fun getPlaylistFromDb(): List<Playlist> {
        val playlistDb = playlistDao.getPlaylist()

        val playlist = mutableListOf<Playlist>()

        playlistDb.forEach {
            val modelItem = it.toModel()
            if(it.categoryId != -1L)
                modelItem.category = categoryDao.getCategoryById(it.categoryId!!).toModel()
        }

        this.playlist = playlist

        return playlist
    }*/
}