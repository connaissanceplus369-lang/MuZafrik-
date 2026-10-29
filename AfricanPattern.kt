package com.muzafrik.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.muzafrik.app.ui.theme.DeepGreen
import com.muzafrik.app.ui.theme.Gold
import com.muzafrik.app.ui.theme.Ocre
import com.muzafrik.app.ui.theme.TerraCotta

private val patternColors = listOf(Ocre, TerraCotta, DeepGreen, Gold)

/** Frise de triangles alternés (motif géométrique inspiré des tissus kente / bogolan). */
@Composable
fun AfricanPatternBand(modifier: Modifier = Modifier, height: Dp = 8.dp) {
    Canvas(modifier = modifier.fillMaxWidth().height(height)) {
        val cell = size.height
        val count = (size.width / cell).toInt() + 1
        for (i in 0 until count) {
            val x = i * cell
            val path = Path().apply {
                if (i % 2 == 0) {
                    moveTo(x, size.height); lineTo(x + cell / 2, 0f); lineTo(x + cell, size.height)
                } else {
                    moveTo(x, 0f); lineTo(x + cell / 2, size.height); lineTo(x + cell, 0f)
                }
                close()
            }
            drawPath(path, patternColors[i % patternColors.size])
        }
    }
}
