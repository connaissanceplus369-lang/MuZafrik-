package com.muzafrik.app.util

import java.text.Normalizer

private val diacritics = Regex("\\p{Mn}+")

/** Minuscules sans accents, pour une recherche tolérante. */
fun String.normalized(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD).replace(diacritics, "").lowercase()

fun formatDuration(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}
