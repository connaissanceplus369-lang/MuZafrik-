package com.muzafrik.app.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzafrik.app.data.repository.MusicRepository
import com.muzafrik.app.domain.model.Album
import com.muzafrik.app.domain.model.Artist
import com.muzafrik.app.domain.model.Playlist
import com.muzafrik.app.domain.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repo: MusicRepository,
) : ViewModel() {

    private fun <T> Flow<T>.asState(initial: T): StateFlow<T> =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial)

    val songs: StateFlow<List<Song>> = repo.songs
    val albums: StateFlow<List<Album>> = repo.albums.asState(emptyList())
    val artists: StateFlow<List<Artist>> = repo.artists.asState(emptyList())
    val playlists: StateFlow<List<Playlist>> = repo.playlists.asState(emptyList())
    val favorites: StateFlow<List<Song>> = repo.favorites.asState(emptyList())
    val favoriteIds: StateFlow<Set<Long>> = repo.favoriteIds.map { it.toSet() }.asState(emptySet())

    private val _loading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _loading.value = true
            repo.refresh()
            _loading.value = false
        }
    }

    fun playlistSongs(id: Long): Flow<List<Song>> = repo.playlistSongs(id)
    fun playlistName(id: Long): Flow<String?> = repo.playlistName(id)

    fun toggleFavorite(songId: Long) {
        viewModelScope.launch { repo.toggleFavorite(songId) }
    }

    fun createPlaylist(name: String, thenAddSongId: Long? = null) {
        viewModelScope.launch {
            val id = repo.createPlaylist(name)
            if (thenAddSongId != null) repo.addToPlaylist(id, thenAddSongId)
        }
    }

    fun deletePlaylist(id: Long) {
        viewModelScope.launch { repo.deletePlaylist(id) }
    }

    fun addToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch { repo.addToPlaylist(playlistId, songId) }
    }

    fun removeFromPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch { repo.removeFromPlaylist(playlistId, songId) }
    }
}
