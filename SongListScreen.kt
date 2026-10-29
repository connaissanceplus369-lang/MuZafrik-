package com.muzafrik.app.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.muzafrik.app.domain.model.Song
import com.muzafrik.app.ui.components.AfricanPatternBand
import com.muzafrik.app.ui.components.PlayAllHeader
import com.muzafrik.app.ui.components.SongsColumn
import com.muzafrik.app.ui.player.PlayerViewModel

/** Écran de détail générique : album, artiste, playlist, favoris. */
@Composable
fun SongListScreen(
    title: String,
    subtitle: String?,
    songs: List<Song>,
    library: LibraryViewModel,
    player: PlayerViewModel,
    onBack: (() -> Unit)? = null,
    onRemove: ((Song) -> Unit)? = null,
    emptyText: String = "Aucun titre",
) {
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(start = if (onBack == null) 20.dp else 4.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour") }
            }
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.headlineSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
            }
        }
        AfricanPatternBand()
        SongsColumn(
            songs = songs,
            library = library,
            player = player,
            modifier = Modifier.weight(1f),
            emptyText = emptyText,
            onRemove = onRemove,
            header = {
                if (songs.isNotEmpty()) item {
                    PlayAllHeader(songs.size, onPlay = { player.play(songs, 0) }, onShuffle = { player.playShuffled(songs) })
                }
            },
        )
    }
}
