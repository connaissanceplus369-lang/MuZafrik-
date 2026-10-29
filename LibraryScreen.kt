package com.muzafrik.app.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muzafrik.app.domain.model.Playlist
import com.muzafrik.app.ui.components.AfricanPatternBand
import com.muzafrik.app.ui.components.Artwork
import com.muzafrik.app.ui.components.ConfirmDialog
import com.muzafrik.app.ui.components.EmptyState
import com.muzafrik.app.ui.components.PlayAllHeader
import com.muzafrik.app.ui.components.SongsColumn
import com.muzafrik.app.ui.components.TextInputDialog
import com.muzafrik.app.ui.player.PlayerViewModel
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(
    library: LibraryViewModel,
    player: PlayerViewModel,
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onOpenAlbum: (Long) -> Unit,
    onOpenArtist: (String) -> Unit,
    onOpenPlaylist: (Long) -> Unit,
) {
    val tabs = listOf("Titres", "Albums", "Artistes", "Playlists")
    val pagerState = rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()
    val loading by library.isLoading.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 4.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append("Mu") }
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) { append("Zafrik") }
                },
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = { library.refresh() }) { Icon(Icons.Filled.Refresh, "Actualiser") }
            IconButton(onClick = onToggleTheme) {
                Icon(if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode, "Changer de thème")
            }
        }
        AfricanPatternBand()
        if (loading) LinearProgressIndicator(Modifier.fillMaxWidth()) else Spacer(Modifier.padding(top = 4.dp))
        TabRow(selectedTabIndex = pagerState.currentPage, containerColor = Color.Transparent) {
            tabs.forEachIndexed { i, label ->
                Tab(
                    selected = pagerState.currentPage == i,
                    onClick = { scope.launch { pagerState.animateScrollToPage(i) } },
                    text = { Text(label, maxLines = 1) },
                )
            }
        }
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            when (page) {
                0 -> SongsTab(library, player)
                1 -> AlbumsTab(library, onOpenAlbum)
                2 -> ArtistsTab(library, onOpenArtist)
                else -> PlaylistsTab(library, onOpenPlaylist)
            }
        }
    }
}

@Composable
private fun SongsTab(library: LibraryViewModel, player: PlayerViewModel) {
    val songs by library.songs.collectAsStateWithLifecycle()
    SongsColumn(
        songs = songs,
        library = library,
        player = player,
        modifier = Modifier.fillMaxSize(),
        emptyText = "Aucune musique trouvée sur cet appareil",
        header = {
            if (songs.isNotEmpty()) item {
                PlayAllHeader(songs.size, onPlay = { player.play(songs, 0) }, onShuffle = { player.playShuffled(songs) })
            }
        },
    )
}

@Composable
private fun AlbumsTab(library: LibraryViewModel, onOpenAlbum: (Long) -> Unit) {
    val albums by library.albums.collectAsStateWithLifecycle()
    if (albums.isEmpty()) {
        EmptyState(Icons.Filled.Album, "Aucun album")
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(albums, key = { it.id }) { album ->
            Column(Modifier.clip(RoundedCornerShape(16.dp)).clickable { onOpenAlbum(album.id) }) {
                Artwork(album.artUri, Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(16.dp)))
                Text(
                    album.title, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 6.dp),
                )
                Text(
                    "${album.artist} · ${album.songCount} titre(s)", maxLines = 1, overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ArtistsTab(library: LibraryViewModel, onOpenArtist: (String) -> Unit) {
    val artists by library.artists.collectAsStateWithLifecycle()
    if (artists.isEmpty()) {
        EmptyState(Icons.Filled.Person, "Aucun artiste")
        return
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
        items(artists, key = { it.name }) { artist ->
            Row(
                Modifier.fillMaxWidth().clickable { onOpenArtist(artist.name) }.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Artwork(artist.artUri, Modifier.size(52.dp).clip(CircleShape))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(artist.name, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "${artist.albumCount} album(s) · ${artist.songCount} titre(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistsTab(library: LibraryViewModel, onOpenPlaylist: (Long) -> Unit) {
    val playlists by library.playlists.collectAsStateWithLifecycle()
    var creating by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<Playlist?>(null) }

    Column(Modifier.fillMaxSize()) {
        FilledTonalButton(onClick = { creating = true }, modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Filled.Add, null, Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Nouvelle playlist")
        }
        if (playlists.isEmpty()) {
            EmptyState(Icons.AutoMirrored.Filled.QueueMusic, "Créez votre première playlist")
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                items(playlists, key = { it.id }) { p ->
                    Row(
                        Modifier.fillMaxWidth().clickable { onOpenPlaylist(p.id) }.padding(start = 16.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.QueueMusic, null,
                            Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)).padding(10.dp),
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(p.name, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyLarge)
                            Text("${p.songCount} titre(s)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { toDelete = p }) { Icon(Icons.Filled.Delete, "Supprimer") }
                    }
                }
            }
        }
    }

    if (creating) {
        TextInputDialog(
            title = "Nouvelle playlist", confirmLabel = "Créer",
            onConfirm = { library.createPlaylist(it); creating = false },
            onDismiss = { creating = false },
        )
    }
    toDelete?.let { p ->
        ConfirmDialog(
            title = "Supprimer la playlist ?", message = "« ${p.name} » sera supprimée (vos fichiers ne sont pas touchés).",
            confirmLabel = "Supprimer",
            onConfirm = { library.deletePlaylist(p.id); toDelete = null },
            onDismiss = { toDelete = null },
        )
    }
}
