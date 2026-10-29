package com.muzafrik.app.domain.model

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val durationMs: Long,
    val uri: String,
    val artUri: String,
    val trackNumber: Int = 0,
)

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val songCount: Int,
    val artUri: String,
)

data class Artist(
    val name: String,
    val songCount: Int,
    val albumCount: Int,
    val artUri: String,
)

/** Résultat direct de la requête Room (colonnes id, name, songCount). */
data class Playlist(
    val id: Long,
    val name: String,
    val songCount: Int,
)
