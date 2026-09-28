package com.example.puzzlesolver.sudoku.data

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList

data class SudokuTile(
    val candidates: PersistentSet<Int> = persistentSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
    val value: Int? = null
) {
    fun setTile(value: Int): SudokuTile = SudokuTile(persistentSetOf(), value)

    fun removeCandidates(values: Set<Int>): SudokuTile = SudokuTile(candidates.removingAll(values), value)

    fun resetTile(): SudokuTile = SudokuTile()
}

data class SudokuBoard(
    private val tiles: PersistentList<SudokuTile> = List(81, { SudokuTile() }).toPersistentList()
) {
    fun tile(id: Int): SudokuTile = tiles[id]

    // returns a new board with the tile at id updated according to func
    fun updateOne(id: Int, func: (SudokuTile) -> SudokuTile): SudokuBoard =
        SudokuBoard(tiles.replacingAt(id, func(tiles[id])))

    // sets the value of the tile at id while respecting the knowledge about possible candidates
    // also updates the candidates to enforce the Sudoku invariant
    fun setTile(id: Int, value: Int): SudokuBoard =
        updateOne(id) { tile ->
            if (value in tile.candidates) {
                tile.setTile(value)
            } else {
                throw IllegalArgumentException("$value is not a possible candidate of $tile: setting it would violate the current candidate knowledge")
            }
        }.updateCandidates()

    // removes candidates of the tile at id
    fun removeCandidates(id: Int, values: Set<Int>): SudokuBoard =
        updateOne(id) { tile ->
            tile.removeCandidates(values)
        }

    // resets the value of the tile at id
    // also recomputes the candidate information because it is stale
    // Important: other previously set tiles will not be cleared and must be cleared manually if desired
    fun resetTile(id: Int): SudokuBoard =
        updateOne(id) { tile ->
            tile.resetTile()
        }
        .resetCandidates()
        .updateCandidates()

    // returns a new board with the tiles updated according to func
    fun updateAll(func: (Int, SudokuTile) -> SudokuTile): SudokuBoard = SudokuBoard(List(81, { i -> func(i, tiles[i]) }).toPersistentList())

    // reset the entire board
    fun reset(): SudokuBoard = SudokuBoard()

    // reset the tiles without values.
    fun resetCandidates(): SudokuBoard =
        updateAll { _, tile -> if (tile.value == null) tile.resetTile() else tile }

    // compute simple candidate information based on the sudoku invariant, i.e.
    // no two values occur in the same row, column or board.
    fun updateCandidates(): SudokuBoard {
        val toRemove = List(81, { mutableSetOf<Int>() })

        for (i in 0..80) {
            val value = tile(i).value
            value?.let { value ->
                val relevantIndices = (rowIndices(rowIndex(i)) + colIndices(colIndex(i)) + blockIndices(blockIndex(i))).distinct()
                for (j in relevantIndices) {
                    toRemove[j].add(value)
                }
            }
        }

        return updateAll { i, tile -> tile.removeCandidates(toRemove[i]) }
    }


    // Various Accessors
    fun rowIndex(id: Int): Int = id / 9
    fun colIndex(id: Int): Int = id % 9
    fun blockIndex(id: Int): Int = 3 * ((id / 9) / 3) + ((id % 9) / 3)

    fun rowIndices(row: Int): List<Int> = List(9, { i -> 9 * row + i })
    fun colIndices(col: Int): List<Int> = List(9, { i -> 9 * i + col })
    fun blockIndices(block: Int): List<Int> =
        List(9, { i -> 9 * 3 * (block / 3) + 3 * (block % 3) + 9 * (i / 3) + (i % 3) })

    fun row(row: Int): List<Int?> = rowIndices(row).map { tile(it).value }
    fun col(row: Int): List<Int?> = colIndices(row).map { tile(it).value }
    fun block(row: Int): List<Int?> = blockIndices(row).map { tile(it).value }
}