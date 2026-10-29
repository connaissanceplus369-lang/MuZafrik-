package com.muzafrik.app.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzafrik.app.domain.model.Song
import com.muzafrik.app.player.PlayerController
import com.muzafrik.app.player.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val controller: PlayerController,
) : ViewModel() {

    val state: StateFlow<PlayerUiState> = controller.state

    // Flux dérivés : évitent de recomposer les listes à chaque tick de progression
    val hasMedia: StateFlow<Boolean> = state.map { it.hasMedia }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
    val nowPlayingId: StateFlow<Long?> = state.map { it.songId }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    val isPlaying: StateFlow<Boolean> = state.map { it.isPlaying }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun play(songs: List<Song>, index: Int) = controller.playSongs(songs, index)
    fun playShuffled(songs: List<Song>) = controller.playShuffled(songs)
    fun togglePlayPause() = controller.togglePlayPause()
    fun next() = controller.next()
    fun previous() = controller.previous()
    fun seekTo(positionMs: Long) = controller.seekTo(positionMs)
    fun toggleShuffle() = controller.toggleShuffle()
    fun cycleRepeat() = controller.cycleRepeat()
}
