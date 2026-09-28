package com.example.puzzlesolver.sudoku.data.heuristics

import com.example.puzzlesolver.sudoku.data.SudokuBoard
import com.example.puzzlesolver.sudoku.data.SudokuTransform
import com.example.puzzlesolver.sudoku.data.TransformType

class NakedX(val tuple: Int) : Heuristic {
    override fun name(): String {
        val tupleString = when(tuple) {
            1 -> "Single"
            2 -> "Pair"
            3 -> "Triple"
            else -> error("Tuple $tuple is not supported. Please extend the case distinction")
        }
        return "Naked $tupleString"
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
    // - Find a set of exactly x tiles whose union of candidates is a set of size y where y <= x.
    // - It follows that y == x and the y candidates must be placed in the x tiles. Otherwise, some of the x tiles will remain empty.
    // - Thus, the y candidates can be eliminated as candidates from all other tiles in the group.
    private fun handleGroup(sudoku: SudokuBoard, group: List<Int>): SudokuTransform? {
        // only look at value-less tiles
        val relevantTiles = group.filter { t -> sudoku.tile(t).value == null }

        // tiles which have x or less candidates, these can be witness tiles for the heuristic
        val interestingTiles = relevantTiles.filter { t -> sudoku.tile(t).candidates.size <= tuple }

        // generate sets of x tiles and the corresponding candidates
        var sets: List<Pair<Set<Int>, Set<Int>>> = listOf(setOf<Int>() to setOf<Int>())
        for (i in 1..tuple) {
            // take sets of size i-1 and create sets of size i

            sets = sets.flatMap { (tiles, candidates) ->
                // the set of candidates will never shrink, so discard sets that are too large
                if (candidates.size <= tuple) {
                    interestingTiles
                        // build sets by considering tile ids in strictly increasing order
                        .filter { t -> t > (tiles.maxOrNull() ?: Int.MIN_VALUE) }
                        .map { t -> tiles + t to candidates.union(sudoku.tile(t).candidates) }
                } else {
                    listOf()
                }
            }
        }
        // consider only sets with exactly y candidates
        sets = sets.filter { (_, candidates) -> candidates.size == tuple }

        for ((tiles, candidates) in sets) {
            computeTransform(sudoku, group, tiles, candidates)?.let { transform -> return transform }
        }
        return null
    }

    private fun computeTransform(sudoku: SudokuBoard, group: List<Int>, tiles: Set<Int>, candidates: Set<Int>): SudokuTransform? {
        when(tuple) {
            // Naked Single: immediately select the correct value
            1 -> return SudokuTransform(
                name = name(),
                highlightedTiles = tiles,
                actions = tiles.associate { id -> id to TransformType.Select(candidates.first()) },
            )
            // Naked Tuple: only transform if some candidates can be ruled out
            else ->
                if (group.subtract(tiles).any { id -> sudoku.tile(id).candidates.intersect(candidates).isNotEmpty() } ) {
                    return SudokuTransform(
                        name = name(),
                        highlightedTiles = group.toSet(),
                        highlightedCandidates = tiles.associate { id -> id to candidates },
                        actions = group.subtract(tiles).associate { id -> id to TransformType.Remove(sudoku.tile(id).candidates.intersect(candidates)) }
                    )
                } else {
                    return null
                }
        }
    }
}