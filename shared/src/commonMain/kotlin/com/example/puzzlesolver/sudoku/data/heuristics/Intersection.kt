package com.example.puzzlesolver.sudoku.data.heuristics

import com.example.puzzlesolver.sudoku.data.SudokuBoard
import com.example.puzzlesolver.sudoku.data.SudokuTransform
import com.example.puzzlesolver.sudoku.data.TransformType

class Intersection() : Heuristic {
    override fun name(): String = "Intersection"

    override fun evaluate(sudoku: SudokuBoard): SudokuTransform? {
        for (i in 0..8) {
            val blockIndices = sudoku.blockIndices(i)
            for (j in 0..8) {
                val rowIndices = sudoku.rowIndices(j)
                handleGroups(sudoku, blockIndices, rowIndices)?.let { transform -> return transform }
                handleGroups(sudoku, rowIndices, blockIndices)?.let { transform -> return transform }
            }
            for (j in 0..8) {
                val colIndices = sudoku.colIndices(j)
                handleGroups(sudoku, blockIndices, colIndices)?.let { transform -> return transform }
                handleGroups(sudoku, colIndices, blockIndices)?.let { transform -> return transform }
            }
        }
        return null
    }

    // Goal:
    // - Find two groups of tiles A and B such that a candidate occurs in (A \cap B) and not in (A \setminus B).
    // - The candidate is confined to (A \cap B) also in group B and thus can be removed from (B \setminus A).
    private fun handleGroups(sudoku: SudokuBoard, group1: List<Int>, group2: List<Int>): SudokuTransform? {
        // split groups into their intersection and individual parts
        val intersection = group1.intersect(group2)
        val group1Only = group1.subtract(intersection)
        val group2Only = group2.subtract(intersection)

        for (i in 1..9) {

            // if in group1, candidate i occurs in the intersection with group2 and only there, i.e. it does not occur in group1Only,
            // then the value must occur in the intersection, i.e. it can be removed from group2Only
            if (intersection.any { id -> i in sudoku.tile(id).candidates }
                && group1Only.all { id -> i !in sudoku.tile(id).candidates }
                && group2Only.any { id -> i in sudoku.tile(id).candidates } ) {
                return SudokuTransform(
                    name = name(),
                    highlightedTiles = group1.union(group2),
                    highlightedCandidates = group1Only.associate { id -> id to setOf(i) }
                            + intersection.filter { id -> i in sudoku.tile(id).candidates }.associate { id -> id to setOf(i) },
                    actions = group2Only.associate { id -> id to TransformType.Remove(setOf(i)) }
                )
            }
        }
        return null
    }
}