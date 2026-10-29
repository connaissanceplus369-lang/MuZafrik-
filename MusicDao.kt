package com.muzafrik.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.muzafrik.app.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    // --- Favoris ---
    @Query("SELECT songId FROM favorites ORDER BY addedAt DESC")
    fun observeFavoriteIds(): Flow<List<Long>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE songId = :songId)")
    suspend fun isFavorite(songId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE songId = :songId")
    suspend fun removeFavorite(songId: Long)

    // --- Playlists ---
    @Query(
        "SELECT p.id AS id, p.name AS name, COUNT(ps.songId) AS songCount " +
            "FROM playlists p LEFT JOIN playlist_songs ps ON ps.playlistId = p.id " +
            "GROUP BY p.id ORDER BY p.createdAt DESC",
    )
    fun observePlaylists(): Flow<List<Playlist>>

    @Query("SELECT name FROM playlists WHERE id = :id")
    fun observePlaylistName(id: Long): Flow<String?>

    @Query("SELECT songId FROM playlist_songs WHERE playlistId = :playlistId ORDER BY addedAt ASC")
    fun observePlaylistSongIds(playlistId: Long): Flow<List<Long>>

    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlaylist(id: Long)

    @Query("UPDATE playlists SET name = :name WHERE id = :id")
    suspend fun renamePlaylist(id: Long, name: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(entry: PlaylistSongEntity)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)
}
