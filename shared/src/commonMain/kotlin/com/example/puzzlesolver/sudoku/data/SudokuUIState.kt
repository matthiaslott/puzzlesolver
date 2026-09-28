package com.example.puzzlesolver.sudoku.data

data class SudokuUIState(
    val sudokuBoard: SudokuBoard = SudokuBoard(),
    val sudokuTransform: SudokuTransform? = null
) {
    fun updateBoard(lambda: (SudokuBoard) -> SudokuBoard): SudokuUIState = SudokuUIState(lambda(sudokuBoard), sudokuTransform)

    fun updateTransform(lambda: (SudokuTransform?) -> SudokuTransform?): SudokuUIState = SudokuUIState(sudokuBoard, lambda(sudokuTransform))
}