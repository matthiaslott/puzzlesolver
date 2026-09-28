package com.example.puzzlesolver.sudoku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.puzzlesolver.customColors
import com.example.puzzlesolver.sudoku.data.SudokuUIState
import com.example.puzzlesolver.sudoku.data.TransformType

@Composable
fun SudokuTile(sudokuUIState: SudokuUIState, tileId: Int, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        // Important: .background().clip().clickable() to ensure that only the ripple effect uses a rounded box.
        modifier = Modifier.fillMaxSize()
            .background(
                if (sudokuUIState.sudokuTransform?.highlightedTiles?.contains(tileId) ?: false)
                    MaterialTheme.colorScheme.surfaceContainerHigh
                else
                    MaterialTheme.colorScheme.surfaceContainerLow
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        if (sudokuUIState.sudokuBoard.tile(tileId).value != null) {
            Text(sudokuUIState.sudokuBoard.tile(tileId).value.toString())
        } else {
            Grid3x3(
                createChild = { i ->
                    val candidate = i + 1
                    val color =
                        if (sudokuUIState.sudokuTransform?.highlightedCandidates[tileId]?.contains(candidate) == true)
                            MaterialTheme.customColors.info
                        else
                            MaterialTheme.colorScheme.outline
                    CenteredTile() {
                        if (candidate in sudokuUIState.sudokuBoard.tile(tileId).candidates) {
                            Text(
                                candidate.toString(),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    lineHeight = 12.sp
                                ),
                                color = when(val action = sudokuUIState.sudokuTransform?.actions[tileId]) {
                                    is TransformType.Select -> if (candidate == action.value) MaterialTheme.customColors.select else color
                                    is TransformType.Remove -> if (candidate in action.values) MaterialTheme.customColors.remove else color
                                    null -> color
                                }
                            )
                        } else {
                            sudokuUIState.sudokuTransform?.highlightedCandidates[tileId]?.let { infoCandidates ->
                                if (candidate in infoCandidates) {
                                    Box(
                                        modifier = Modifier.size(10.dp).border(
                                            1.dp,
                                            color = MaterialTheme.customColors.info
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}