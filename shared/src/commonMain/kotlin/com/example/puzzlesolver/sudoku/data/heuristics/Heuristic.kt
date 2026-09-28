package com.example.puzzlesolver.sudoku.data.heuristics

import com.example.puzzlesolver.sudoku.data.SudokuBoard
import com.example.puzzlesolver.sudoku.data.SudokuTransform

interface Heuristic {
    // heuristic name
    fun name(): String

    // evaluate heuristic on sudoku. returns null if no changes are suggested
    fun evaluate(sudoku: SudokuBoard): SudokuTransform?
}