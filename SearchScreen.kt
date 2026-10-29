package com.muzafrik.app.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muzafrik.app.ui.components.EmptyState
import com.muzafrik.app.ui.components.SongsColumn
import com.muzafrik.app.ui.library.LibraryViewModel
import com.muzafrik.app.ui.player.PlayerViewModel

@Composable
fun SearchScreen(
    library: LibraryViewModel,
    player: PlayerViewModel,
    onOpenAlbum: (Long) -> Unit,
    onOpenArtist: (String) -> Unit,
    vm: SearchViewModel = hiltViewModel(),
) {
    var text by rememberSaveable { mutableStateOf("") }
    val results by vm.results.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.setQuery(text) }

    Column(Modifier.fillMaxSize()) {
        Text(
            "Recherche",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 8.dp),
        )
        OutlinedTextField(
            value = text,
            onValueChange = { text = it; vm.setQuery(it) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            placeholder = { Text("Titres, albums, artistes…") },
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            trailingIcon = if (text.isNotEmpty()) {
                @Composable {
                    IconButton(onClick = { text = ""; vm.setQuery("") }) { Icon(Icons.Filled.Close, "Effacer") }
                }
            } else null,
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
        )
        if (text.isBlank()) {
            EmptyState(Icons.Filled.Search, "Cherchez un titre, un album ou un artiste")
        } else {
            SongsColumn(
                songs = results.songs,
                library = library,
                player = player,
                modifier = Modifier.weight(1f).padding(top = 8.dp),
                emptyText = "Aucun résultat",
                header = {
                    if (results.albums.isNotEmpty() || results.artists.isNotEmpty()) item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(results.albums.take(10), key = { "album-${it.id}" }) { a ->
                                AssistChip(
                                    onClick = { onOpenAlbum(a.id) },
                                    label = { Text(a.title, maxLines = 1) },
                                    leadingIcon = { Icon(Icons.Filled.Album, null, Modifier.size(18.dp)) },
                                )
                            }
                            items(results.artists.take(10), key = { "artist-${it.name}" }) { a ->
                                AssistChip(
                                    onClick = { onOpenArtist(a.name) },
                                    label = { Text(a.name, maxLines = 1) },
                                    leadingIcon = { Icon(Icons.Filled.Person, null, Modifier.size(18.dp)) },
                                )
                            }
                        }
                    }
                },
            )
        }
    }
}
