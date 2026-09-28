package com.example.puzzlesolver.sudoku.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalGridApi::class)
@Composable
fun Grid3x3(gap: Dp = 0.dp, createChild: @Composable (Int) -> Unit, modifier: Modifier = Modifier) {
    Grid(
        config = {
            repeat(3) {
                column(1f/3f)
                row(1f/3f)
            }
            gap(gap)
        },
        content = {
            List (3 * 3) { i ->
                createChild(i)
            }
        },
        modifier = modifier
    )
}

@Composable
fun CenteredTile(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
    ) {
        content()
    }
}