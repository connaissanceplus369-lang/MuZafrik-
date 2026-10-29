package com.muzafrik.app.data.repository

import com.muzafrik.app.data.local.FavoriteEntity
import com.muzafrik.app.data.local.MusicDao
import com.muzafrik.app.data.local.PlaylistEntity
import com.muzafrik.app.data.local.PlaylistSongEntity
import com.muzafrik.app.data.media.MediaStoreDataSource
import com.muzafrik.app.domain.model.Album
import com.muzafrik.app.domain.model.Artist
import com.muzafrik.app.domain.model.Playlist
import com.muzafrik.app.domain.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val mediaStore: MediaStoreDataSource,
    private val dao: MusicDao,
) {
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    suspend fun refresh() {
        _songs.value = mediaStore.loadSongs()
    }

    val albums: Flow<List<Album>> = songs.map { list ->
        list.groupBy { it.albumId }
            .map { (id, s) -> Album(id, s.first().album, s.first().artist, s.size, s.first().artUri) }
            .sortedBy { it.title.lowercase() }
    }

    val artists: Flow<List<Artist>> = songs.map { list ->
        list.groupBy { it.artist }
            .map { (name, s) -> Artist(name, s.size, s.map { it.albumId }.distinct().size, s.first().artUri) }
            .sortedBy { it.name.lowercase() }
    }

    // --- Favoris ---
    val favoriteIds: Flow<List<Long>> = dao.observeFavoriteIds()

    val favorites: Flow<List<Song>> = combine(songs, dao.observeFavoriteIds()) { all, ids ->
        val byId = all.associateBy { it.id }
        ids.mapNotNull { byId[it] }
    }

    suspend fun toggleFavorite(songId: Long) {
        if (dao.isFavorite(songId)) dao.removeFavorite(songId) else dao.addFavorite(FavoriteEntity(songId))
    }

    // --- Playlists ---
    val playlists: Flow<List<Playlist>> = dao.observePlaylists()

    fun playlistName(id: Long): Flow<String?> = dao.observePlaylistName(id)

    fun playlistSongs(id: Long): Flow<List<Song>> =
        combine(songs, dao.observePlaylistSongIds(id)) { all, ids ->
            val byId = all.associateBy { it.id }
            ids.mapNotNull { byId[it] }
        }

    suspend fun createPlaylist(name: String): Long = dao.insertPlaylist(PlaylistEntity(name = name))
    suspend fun deletePlaylist(id: Long) = dao.deletePlaylist(id)
    suspend fun renamePlaylist(id: Long, name: String) = dao.renamePlaylist(id, name)
    suspend fun addToPlaylist(playlistId: Long, songId: Long) =
        dao.addSongToPlaylist(PlaylistSongEntity(playlistId, songId))
    suspend fun removeFromPlaylist(playlistId: Long, songId: Long) =
        dao.removeSongFromPlaylist(playlistId, songId)
}
