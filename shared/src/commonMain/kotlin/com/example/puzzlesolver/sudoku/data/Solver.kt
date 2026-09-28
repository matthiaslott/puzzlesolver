package com.example.puzzlesolver.sudoku.data

import com.example.puzzlesolver.sudoku.data.heuristics.Heuristic
import com.example.puzzlesolver.sudoku.data.heuristics.HiddenX
import com.example.puzzlesolver.sudoku.data.heuristics.Intersection
import com.example.puzzlesolver.sudoku.data.heuristics.NakedX

object Solver {
    private val heuristics = mutableListOf<Heuristic>()

    // initial heuristics configuration, TODO: consider user-specified heuristics in the future, e.g. different order of heuristics.
    init {
        heuristics.add(NakedX(1))
        heuristics.add(HiddenX(1))

        heuristics.add(NakedX(2))
        heuristics.add(HiddenX(2))

        heuristics.add(Intersection())

        heuristics.add(NakedX(3))
        heuristics.add(HiddenX(3))
    }

    //
    fun solve(sudoku: SudokuBoard): SudokuTransform? {
        // sequentially try out the heuristics
        for (heuristic in heuristics) {
            val transform = heuristic.evaluate(sudoku)
            if (transform != null) {
                return transform
            }
        }

        return null
    }
}