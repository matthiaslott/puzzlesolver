package com.example.puzzlesolver

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomColors(
    val select: Color,
    val remove: Color,
    val info: Color,
)

val LightCustomColors = CustomColors(
    select = Color(0xFF388E3C),
    remove = Color(0xFFD32F2F),
    info = Color(0xFF1976D2),
)

val DarkCustomColors = CustomColors(
    select = Color(0xFF81C784),
    remove = Color(0xFFE57373),
    info = Color(0xFF64B5F6),
)

val LocalCustomColors = staticCompositionLocalOf<CustomColors> {
    error("CustomColors not provided")
}

val MaterialTheme.customColors: CustomColors
    @Composable
    get() = LocalCustomColors.current