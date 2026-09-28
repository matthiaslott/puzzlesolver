package com.example.puzzlesolver.sudoku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.puzzlesolver.customColors
import com.example.puzzlesolver.sudoku.data.SudokuUIState

@Composable
fun SudokuSelector(sudokuUIState: SudokuUIState, tileId: Int, size: DpSize, offset: DpOffset, onClick: (Int) -> Unit) {
    Grid3x3(
        gap = 0.dp,
        createChild = { i ->
            val candidate = i + 1
            val isEnabled =
                candidate in sudokuUIState.sudokuBoard.tile(tileId).candidates // current number is a candidate
                        || sudokuUIState.sudokuBoard.tile(tileId).value == candidate // ensure deletion button is enabled
            CenteredTile(
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    .clickable(
                        enabled = isEnabled,
                        onClick = { onClick(candidate) })
            ) {
                if (sudokuUIState.sudokuBoard.tile(tileId).value == candidate) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.customColors.remove
                    )
                } else {
                    Text(
                        candidate.toString(),
                        color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                    )
                }
            }
        },
        modifier = Modifier
            .requiredSize(size)
            .offset(offset.x, offset.y)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
    )
}