package com.muzafrik.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzafrik.app.data.repository.MusicRepository
import com.muzafrik.app.domain.model.Album
import com.muzafrik.app.domain.model.Artist
import com.muzafrik.app.domain.model.Song
import com.muzafrik.app.util.normalized
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SearchResults(
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    repo: MusicRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")

    fun setQuery(value: String) {
        query.value = value
    }

    val results: StateFlow<SearchResults> = combine(query, repo.songs) { q, songs ->
        val needle = q.normalized().trim()
        if (needle.isEmpty()) {
            SearchResults()
        } else {
            SearchResults(
                songs = songs.filter {
                    it.title.normalized().contains(needle) ||
                        it.artist.normalized().contains(needle) ||
                        it.album.normalized().contains(needle)
                },
                albums = songs.filter { it.album.normalized().contains(needle) }
                    .distinctBy { it.albumId }
                    .map { Album(it.albumId, it.album, it.artist, 0, it.artUri) },
                artists = songs.filter { it.artist.normalized().contains(needle) }
                    .distinctBy { it.artist }
                    .map { Artist(it.artist, 0, 0, it.artUri) },
            )
        }
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchResults())
}
