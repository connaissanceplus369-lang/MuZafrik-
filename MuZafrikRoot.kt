package com.muzafrik.app.ui

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.muzafrik.app.data.settings.ThemeMode
import com.muzafrik.app.ui.library.LibraryScreen
import com.muzafrik.app.ui.library.LibraryViewModel
import com.muzafrik.app.ui.library.SongListScreen
import com.muzafrik.app.ui.player.MiniPlayer
import com.muzafrik.app.ui.player.NowPlayingScreen
import com.muzafrik.app.ui.player.PlayerViewModel
import com.muzafrik.app.ui.search.SearchScreen
import com.muzafrik.app.ui.settings.SettingsViewModel

object Routes {
    const val LIBRARY = "library"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val ALBUM = "album/{id}"
    const val ARTIST = "artist/{name}"
    const val PLAYLIST = "playlist/{id}"
}

private data class BottomItem(val route: String, val label: String, val icon: ImageVector)

private val bottomItems = listOf(
    BottomItem(Routes.LIBRARY, "Bibliothèque", Icons.Filled.LibraryMusic),
    BottomItem(Routes.SEARCH, "Recherche", Icons.Filled.Search),
    BottomItem(Routes.FAVORITES, "Favoris", Icons.Filled.Favorite),
)

@Composable
fun MuZafrikRoot(settings: SettingsViewModel) {
    val library: LibraryViewModel = hiltViewModel()
    val player: PlayerViewModel = hiltViewModel()
    val nav = rememberNavController()

    val hasMedia by player.hasMedia.collectAsStateWithLifecycle()
    val themeMode by settings.themeMode.collectAsStateWithLifecycle()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val showPlayer = expanded && hasMedia

    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route
    val topLevel = route == null || bottomItems.any { it.route == route }

    BackHandler(enabled = showPlayer) { expanded = false }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                Column(if (topLevel) Modifier else Modifier.navigationBarsPadding()) {
                    AnimatedVisibility(
                        visible = hasMedia,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut(),
                    ) {
                        MiniPlayer(player = player, onClick = { expanded = true })
                    }
                    if (topLevel) {
                        NavigationBar {
                            bottomItems.forEach { item ->
                                NavigationBarItem(
                                    selected = route == item.route,
                                    onClick = {
                                        nav.navigate(item.route) {
                                            popUpTo(Routes.LIBRARY) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(item.icon, item.label) },
                                    label = { Text(item.label) },
                                )
                            }
                        }
                    }
                }
            },
        ) { padding ->
            NavHost(nav, startDestination = Routes.LIBRARY, modifier = Modifier.padding(padding)) {
                composable(Routes.LIBRARY) {
                    LibraryScreen(
                        library = library,
                        player = player,
                        isDark = isDark,
                        onToggleTheme = { settings.setThemeMode(if (isDark) ThemeMode.LIGHT else ThemeMode.DARK) },
                        onOpenAlbum = { nav.navigate("album/$it") },
                        onOpenArtist = { nav.navigate("artist/${Uri.encode(it)}") },
                        onOpenPlaylist = { nav.navigate("playlist/$it") },
                    )
                }
                composable(Routes.SEARCH) {
                    SearchScreen(
                        library = library,
                        player = player,
                        onOpenAlbum = { nav.navigate("album/$it") },
                        onOpenArtist = { nav.navigate("artist/${Uri.encode(it)}") },
                    )
                }
                composable(Routes.FAVORITES) {
                    val favorites by library.favorites.collectAsStateWithLifecycle()
                    SongListScreen(
                        title = "Favoris",
                        subtitle = null,
                        songs = favorites,
                        library = library,
                        player = player,
                        emptyText = "Touchez le cœur d'un titre pour l'ajouter ici",
                    )
                }
                composable(Routes.ALBUM, arguments = listOf(navArgument("id") { type = NavType.LongType })) { entry ->
                    val id = entry.arguments?.getLong("id") ?: return@composable
                    val all by library.songs.collectAsStateWithLifecycle()
                    val songs = remember(all, id) {
                        all.filter { it.albumId == id }
                            .sortedWith(compareBy({ it.trackNumber }, { it.title.lowercase() }))
                    }
                    SongListScreen(
                        title = songs.firstOrNull()?.album ?: "Album",
                        subtitle = songs.firstOrNull()?.artist,
                        songs = songs,
                        library = library,
                        player = player,
                        onBack = { nav.popBackStack() },
                    )
                }
                composable(Routes.ARTIST, arguments = listOf(navArgument("name") { type = NavType.StringType })) { entry ->
                    val name = entry.arguments?.getString("name") ?: return@composable
                    val all by library.songs.collectAsStateWithLifecycle()
                    val songs = remember(all, name) { all.filter { it.artist == name } }
                    SongListScreen(
                        title = name,
                        subtitle = "${songs.size} titre(s)",
                        songs = songs,
                        library = library,
                        player = player,
                        onBack = { nav.popBackStack() },
                    )
                }
                composable(Routes.PLAYLIST, arguments = listOf(navArgument("id") { type = NavType.LongType })) { entry ->
                    val id = entry.arguments?.getLong("id") ?: return@composable
                    val songs by remember(id) { library.playlistSongs(id) }
                        .collectAsStateWithLifecycle(initialValue = emptyList())
                    val name by remember(id) { library.playlistName(id) }
                        .collectAsStateWithLifecycle(initialValue = null)
                    SongListScreen(
                        title = name ?: "Playlist",
                        subtitle = "${songs.size} titre(s)",
                        songs = songs,
                        library = library,
                        player = player,
                        onBack = { nav.popBackStack() },
                        onRemove = { library.removeFromPlaylist(id, it.id) },
                        emptyText = "Ajoutez des titres via le menu ⋮ d'un morceau",
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showPlayer,
            enter = slideInVertically(tween(350)) { it } + fadeIn(tween(250)),
            exit = slideOutVertically(tween(300)) { it } + fadeOut(tween(250)),
        ) {
            NowPlayingScreen(player = player, library = library, onCollapse = { expanded = false })
        }
    }
}
