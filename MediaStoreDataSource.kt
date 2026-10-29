package com.muzafrik.app.data.media

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.muzafrik.app.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/** Scan des musiques locales via MediaStore. */
@Singleton
class MediaStoreDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun loadSongs(): List<Song> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TRACK,
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= ?"
        val result = mutableListOf<Song>()
        try {
            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                arrayOf(MIN_DURATION_MS.toString()),
                null,
            )?.use { c ->
                val idCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumIdCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val durationCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val trackCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
                while (c.moveToNext()) {
                    val id = c.getLong(idCol)
                    val albumId = c.getLong(albumIdCol)
                    val artist = c.getString(artistCol)
                    val album = c.getString(albumCol)
                    result += Song(
                        id = id,
                        title = c.getString(titleCol)?.takeIf { it.isNotBlank() } ?: "Titre inconnu",
                        artist = if (artist.isNullOrBlank() || artist == UNKNOWN) "Artiste inconnu" else artist,
                        album = if (album.isNullOrBlank() || album == UNKNOWN) "Album inconnu" else album,
                        albumId = albumId,
                        durationMs = c.getLong(durationCol),
                        uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).toString(),
                        artUri = ContentUris.withAppendedId(ALBUM_ART_URI, albumId).toString(),
                        trackNumber = c.getInt(trackCol),
                    )
                }
            }
        } catch (_: SecurityException) {
            // Permission non accordée : bibliothèque vide
        }
        result.sortedBy { it.title.lowercase() }
    }

    private companion object {
        const val MIN_DURATION_MS = 30_000L
        const val UNKNOWN = "<unknown>"
        val ALBUM_ART_URI: Uri = Uri.parse("content://media/external/audio/albumart")
    }
}
