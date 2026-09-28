package com.example.puzzlesolver.sudoku.data

import kotlinx.collections.immutable.persistentMapOf

data class SudokuTransform(
    //
    val name: String,
    //
    val highlightedTiles: Set<Int> = emptySet(),
    val highlightedCandidates: Map<Int, Set<Int>> = persistentMapOf(),
    //
    val actions: Map<Int, TransformType> = persistentMapOf()
) {
    // whether applying the transform at a specific tile changes the board
    fun appliesAt(sudoku: SudokuBoard, id: Int): Boolean = actions[id]?.appliesAt(sudoku, id) ?: false

    // whether applying the transform changes the board
    fun applies(sudoku: SudokuBoard): Boolean = actions.any { (id, transform) -> transform.appliesAt(sudoku, id) }

    // apply the transform at a specific tile
    fun applyAt(sudoku: SudokuBoard, id: Int): SudokuBoard {
        if (appliesAt(sudoku, id)) {
            actions[id]?.let { action ->
                return action.apply(sudoku, id)
            }
        }
        return sudoku
    }

    // apply the transform to the entire board
    fun apply(sudoku: SudokuBoard): SudokuBoard {
        var newSudoku = sudoku
        for (i in 0..80) {
            newSudoku = applyAt(newSudoku, i)
        }
        return newSudoku
    }
}

sealed class TransformType {
    class Select(val value: Int) : TransformType()
    class Remove(val values: Set<Int>) : TransformType()

    fun appliesAt(sudoku: SudokuBoard, id: Int): Boolean {
        return when(this) {
            is Select -> sudoku.tile(id).value == null
            is Remove -> sudoku.tile(id).candidates.intersect(values).isNotEmpty()
        }
    }

    fun apply(sudoku: SudokuBoard, id: Int): SudokuBoard {
        return when(this) {
            is Select -> sudoku.setTile(id, value)
            is Remove -> sudoku.removeCandidates(id, values)
        }
    }
}