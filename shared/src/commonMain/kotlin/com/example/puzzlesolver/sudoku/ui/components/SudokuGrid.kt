package com.example.puzzlesolver.sudoku.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.puzzlesolver.sudoku.data.SudokuUIState

private val BLOCK_GAP = 3.dp
private val TILE_GAP = 1.dp


@Composable
fun SudokuGrid(
    sudokuUIState: SudokuUIState,
    // callback when clicking a tile. return value indicates whether selector should open
    onTileClick: (Int) -> Boolean,
    // callback when selecting a value using the selector
    onValueSelect: (Int, Int) -> Unit,
) {
    val density = LocalDensity.current
    val statusBarHeightPx = WindowInsets.statusBars.getTop(density)
    //
    var tileSize by remember { mutableStateOf(DpSize(0.dp, 0.dp)) }
    var gridOffset by remember { mutableStateOf(DpOffset(0.dp, 0.dp)) }
    var selectedTile: Int? by remember { mutableStateOf(null) }
    Grid3x3(
        gap = BLOCK_GAP,
        createChild = { block ->
            val blockRow = block / 3
            val blockCol = block % 3
            Grid3x3(
                gap = TILE_GAP,
                createChild = { tile ->
                    val tileRow = 3 * blockRow + (tile / 3)
                    val tileCol = 3 * blockCol + (tile % 3)
                    val tileId = 9 * tileRow + tileCol
                    SudokuTile(
                        sudokuUIState,
                        tileId,
                        onClick = {
                            if (onTileClick(tileId)) {
                                selectedTile = tileId
                            }
                        },
                    )
                },
            )
        },
        modifier = Modifier.aspectRatio(1f)
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(20.dp)
            ) // same radius as in clip() below creates artifacts on screen
            .clip(RoundedCornerShape(16.dp))
            .onGloballyPositioned({ coordinates ->
                with(density) {
                    val cellWidth =
                        (coordinates.size.width.toDp() - BLOCK_GAP * 2 - TILE_GAP * 6) / 9
                    val cellHeight =
                        (coordinates.size.height.toDp() - BLOCK_GAP * 2 - TILE_GAP * 6) / 9
                    tileSize = DpSize(cellWidth, cellHeight)
                    val xOffset = coordinates.positionInRoot().x.toDp()
                    val yOffset = coordinates.positionInRoot().y.toDp()
                    // Important: Currently, it seems impossible to specify usePlatformInsets = false in DialogProperties.
                    // As a result, one must manually account for the size of the statusBar.
                    gridOffset = DpOffset(xOffset, yOffset - statusBarHeightPx.toDp())
                }
            })

    )

    val closeOverlay = { selectedTile = null }
    selectedTile?.let { selectedTile ->
        Dialog(
            onDismissRequest = closeOverlay,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .clickable(
                        interactionSource = null,
                        indication = null,
                        onClick = closeOverlay
                    )
            ) {
                SudokuSelector(
                    sudokuUIState,
                    selectedTile,
                    tileSize * 3,
                    gridOffset + offsetFromTileId(selectedTile, tileSize),
                    onClick = { value ->
                        closeOverlay()
                        onValueSelect(selectedTile, value)
                    }
                )
            }
        }
    }
}


// compute position of overlay based on tileId and tileSize
fun offsetFromTileId(tileId: Int, tileSize: DpSize): DpOffset {
    // compute x position of selected tile
    val col = tileId % 9
    val xBlockGaps = col / 3
    val xTileGaps = col - xBlockGaps
    val xTilePos = tileSize.width * col + BLOCK_GAP * xBlockGaps + TILE_GAP * xTileGaps
    // ensure overlay is centered horizontally with respect to tile
    val xOffset = (xTilePos - tileSize.width)
        .coerceIn(0.dp, tileSize.width * 6 + BLOCK_GAP * 2 + TILE_GAP * 6) // clamp at grid edge

    // compute y position of selected tile
    val row = tileId / 9
    val yBlockGaps = row / 3
    val yTileGaps = row - yBlockGaps
    val yTilePos = tileSize.height * row + BLOCK_GAP * yBlockGaps + TILE_GAP * yTileGaps
    // ensure overlay is below tile
    val yOffset = yTilePos + tileSize.height

    return DpOffset(xOffset, yOffset)
}