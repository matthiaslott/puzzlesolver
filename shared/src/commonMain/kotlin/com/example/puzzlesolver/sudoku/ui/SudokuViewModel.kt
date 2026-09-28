package com.example.puzzlesolver.sudoku.ui

import androidx.lifecycle.ViewModel
import com.example.puzzlesolver.sudoku.data.Solver
import com.example.puzzlesolver.sudoku.data.SudokuUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class SudokuViewModel : ViewModel() {
    private val _sudokuUIState : MutableStateFlow<SudokuUIState> = MutableStateFlow(SudokuUIState())
    val sudokuUIState: StateFlow<SudokuUIState> = _sudokuUIState.asStateFlow()

    // run the solver to provide a suggestion to the user
    private fun onChange() {
        val transform = Solver.solve(_sudokuUIState.value.sudokuBoard)
        _sudokuUIState.value = _sudokuUIState.value.updateTransform { transform }
    }

    // callback for clicking a sudoku tile
    fun onTileClick(tileId: Int): Boolean {
        val board = _sudokuUIState.value.sudokuBoard
        val transform = _sudokuUIState.value.sudokuTransform
        transform?.let { transform ->
            if (transform.appliesAt(board, tileId)) {
                _sudokuUIState.value = _sudokuUIState.value.updateBoard { transform.applyAt(board, tileId) }
                onChange()
                return false
            }
        }
        return true
    }

    // callback when selecting a value using the sudoku selector
    fun onValueSelect(tileId: Int, value: Int) {
        if (_sudokuUIState.value.sudokuBoard.tile(tileId).value == value) {
            _sudokuUIState.value = _sudokuUIState.value.updateBoard { board -> board.resetTile(tileId) }
        } else {
            _sudokuUIState.value = _sudokuUIState.value.updateBoard { board -> board.setTile(tileId, value) }
        }
        onChange()
    }

    // reset entire state
    fun reset() {
        _sudokuUIState.value = SudokuUIState()
    }

    // apply suggestion
    fun step() {
        val board = _sudokuUIState.value.sudokuBoard
        val transform = _sudokuUIState.value.sudokuTransform
        transform?.let {
            _sudokuUIState.value = SudokuUIState(transform.apply(board), null)
            onChange()
        }
    }
}