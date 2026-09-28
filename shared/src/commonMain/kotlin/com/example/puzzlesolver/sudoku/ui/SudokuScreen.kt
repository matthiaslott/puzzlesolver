package com.example.puzzlesolver.sudoku.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.puzzlesolver.sudoku.ui.components.SudokuGrid
import org.jetbrains.compose.resources.stringResource
import puzzlesolver.shared.generated.resources.Res
import puzzlesolver.shared.generated.resources.reset
import puzzlesolver.shared.generated.resources.step
import puzzlesolver.shared.generated.resources.suggestion

@Composable
fun SudokuScreen() {
    val sudokuViewModel: SudokuViewModel = viewModel { SudokuViewModel() }
    val sudokuUIState = sudokuViewModel.sudokuUIState.collectAsState().value
    Column(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "${stringResource(Res.string.suggestion)}: ${sudokuUIState.sudokuTransform?.name ?: "None"}"
        )
        SudokuGrid(
            sudokuUIState,
            onTileClick = { tileId -> sudokuViewModel.onTileClick(tileId) },
            onValueSelect = { tileId, value -> sudokuViewModel.onValueSelect(tileId, value) }
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilledTonalButton(
                onClick = { sudokuViewModel.reset() }
            ) {
                Text(stringResource(Res.string.reset))
            }

            FilledTonalButton(
                enabled = sudokuUIState.sudokuTransform != null,
                onClick = { sudokuViewModel.step() }
            ) {
                Text(stringResource(Res.string.step))
            }
        }

    }
}