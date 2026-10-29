package com.muzafrik.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muzafrik.app.domain.model.Song
import com.muzafrik.app.ui.library.LibraryViewModel
import com.muzafrik.app.ui.player.PlayerViewModel

@Composable
fun SongRow(
    song: Song,
    isCurrent: Boolean,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onRemove: (() -> Unit)? = null,
) {
    var menuOpen by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Artwork(song.artUri, Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                song.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
            Text(
                "${song.artist} · ${song.album}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (isCurrent && isPlaying) {
            Icon(Icons.Filled.GraphicEq, contentDescription = "En lecture", tint = MaterialTheme.colorScheme.primary)
        }
        IconButton(onClick = onToggleFavorite) {
            Icon(
                if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Favori",
                tint = if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Box {
            IconButton(onClick = { menuOpen = true }) { Icon(Icons.Filled.MoreVert, contentDescription = "Plus") }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text("Ajouter à une playlist") },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.PlaylistAdd, null) },
                    onClick = { menuOpen = false; onAddToPlaylist() },
                )
                if (onRemove != null) {
                    DropdownMenuItem(
                        text = { Text("Retirer de la playlist") },
                        leadingIcon = { Icon(Icons.Filled.Delete, null) },
                        onClick = { menuOpen = false; onRemove() },
                    )
                }
            }
        }
    }
}

@Composable
fun PlayAllHeader(count: Int, onPlay: () -> Unit, onShuffle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(onClick = onPlay) {
            Icon(Icons.Filled.PlayArrow, null, Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Lire")
        }
        FilledTonalButton(onClick = onShuffle) {
            Icon(Icons.Filled.Shuffle, null, Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Aléatoire")
        }
        Spacer(Modifier.weight(1f))
        Text("$count titre(s)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Liste de titres réutilisable (bibliothèque, détails, favoris, recherche). */
@Composable
fun SongsColumn(
    songs: List<Song>,
    library: LibraryViewModel,
    player: PlayerViewModel,
    modifier: Modifier = Modifier,
    emptyText: String = "Aucun titre",
    onRemove: ((Song) -> Unit)? = null,
    header: (LazyListScope.() -> Unit)? = null,
) {
    val nowPlayingId by player.nowPlayingId.collectAsStateWithLifecycle()
    val isPlaying by player.isPlaying.collectAsStateWithLifecycle()
    val favoriteIds by library.favoriteIds.collectAsStateWithLifecycle()
    val playlists by library.playlists.collectAsStateWithLifecycle()
    var pickerSong by remember { mutableStateOf<Song?>(null) }

    LazyColumn(modifier = modifier, contentPadding = PaddingValues(bottom = 16.dp)) {
        header?.invoke(this)
        if (songs.isEmpty()) item { EmptyState(Icons.Filled.MusicNote, emptyText) }
        itemsIndexed(songs, key = { _, s -> s.id }) { index, song ->
            SongRow(
                song = song,
                isCurrent = nowPlayingId == song.id,
                isPlaying = isPlaying,
                isFavorite = song.id in favoriteIds,
                onClick = { player.play(songs, index) },
                onToggleFavorite = { library.toggleFavorite(song.id) },
                onAddToPlaylist = { pickerSong = song },
                onRemove = onRemove?.let { remove -> { remove(song) } },
            )
        }
    }

    pickerSong?.let { song ->
        AddToPlaylistDialog(
            playlists = playlists,
            onPick = { library.addToPlaylist(it.id, song.id); pickerSong = null },
            onCreate = { name -> library.createPlaylist(name, song.id); pickerSong = null },
            onDismiss = { pickerSong = null },
        )
    }
}
