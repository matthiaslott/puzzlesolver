package com.example.puzzlesolver.sudoku.data.heuristics

import com.example.puzzlesolver.sudoku.data.SudokuBoard
import com.example.puzzlesolver.sudoku.data.SudokuTransform
import com.example.puzzlesolver.sudoku.data.TransformType

class HiddenX(val tuple: Int) : Heuristic {
    override fun name(): String {
        val tupleString = when (tuple) {
            1 -> "Single"
            2 -> "Pair"
            3 -> "Triple"
            else -> error("Tuple $tuple is not supported. Please extend the case distinction")
        }
        return "Hidden $tupleString"
    }

    override fun evaluate(sudoku: SudokuBoard): SudokuTransform? {
        // run logic on every group of tiles
        for (i in 0..8) {
            handleGroup(sudoku, sudoku.blockIndices(i))?.let { transform -> return transform }
        }
        for (i in 0..8) {
            handleGroup(sudoku, sudoku.rowIndices(i))?.let { transform -> return transform }
        }
        for (i in 0..8) {
            handleGroup(sudoku, sudoku.colIndices(i))?.let { transform -> return transform }
        }
        return null
    }

    // Goal:
    // - Find a set of exactly x candidates whose union of tiles is a set of size y where y <= x.
    // - The only way to place the x candidates is in the y tiles (and that y == x).
    // - Thus, all other candidates can be eliminated as candidates from the y tiles.
    private fun handleGroup(sudoku: SudokuBoard, group: List<Int>): SudokuTransform? {
        // only look at value-less tiles
        val relevantTiles = group.filter { t -> sudoku.tile(t).value == null }

        // create mapping from candidates to tiles
        val candidatesToTiles = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9).associate { c -> c to relevantTiles.filter { id -> c in sudoku.tile(id).candidates } }

        // candidates which have x or less tiles but haven't been placed yet, these can be witness candidates for the heuristic
        val interestingCandidates = candidatesToTiles.filter { (_, t) -> t.isNotEmpty() && t.size <= tuple }.keys

        // generate sets of x candidates and the corresponding tiles
        var sets: List<Pair<Set<Int>, Set<Int>>> = listOf(setOf<Int>() to setOf<Int>())
        for (i in 1..tuple) {
            // take sets of size i-1 and create sets of size i

            sets = sets.flatMap { (candidates, tiles) ->
                // the set of tiles will never shrink, so discard sets that are too large
                if (tiles.size <= tuple) {
                    interestingCandidates
                        // build sets by considering candidates in strictly increasing order
                        .filter { c -> c > (candidates.maxOrNull() ?: Int.MIN_VALUE) }
                        .map { c -> candidates + c to tiles.union(candidatesToTiles[c]?: setOf()) }
                } else {
                    listOf()
                }
            }
        }
        // consider only sets with exactly y tiles
        sets = sets.filter { (_, tiles) -> tiles.size == tuple }

        for ((candidates, tiles) in sets) {
            computeTransform(sudoku, group, tiles, candidates)?.let { transform -> return transform }
        }
        return null
    }

    private fun computeTransform(sudoku: SudokuBoard, group: List<Int>, tiles: Set<Int>, candidates: Set<Int>): SudokuTransform? {
        when(tuple) {
            // Hidden Single: immediately select the correct value
            1 -> return SudokuTransform(
                name = name(),
                highlightedTiles = group.toSet(),
                highlightedCandidates = group.subtract(tiles).associate { otherId -> otherId to candidates },
                actions = tiles.associate { id -> id to TransformType.Select(candidates.first()) },
            )
            // Hidden Tuple: only transform if some candidates can be ruled out
            else ->
                if (tiles.any { id -> sudoku.tile(id).candidates.subtract(candidates).isNotEmpty() } ) {
                    return SudokuTransform(
                        name = name(),
                        highlightedTiles = group.toSet(),
                        highlightedCandidates = group.associate { id -> id to candidates },
                        actions = tiles.associate { id -> id to TransformType.Remove(sudoku.tile(id).candidates.subtract(candidates)) }
                    )
                } else {
                    return null
                }
        }
    }
}