package com.example.puzzlesolver

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

import puzzlesolver.shared.generated.resources.Res
import puzzlesolver.shared.generated.resources.sudoku_label
import puzzlesolver.shared.generated.resources.sudoku_title
import puzzlesolver.shared.generated.resources.tbd_label
import puzzlesolver.shared.generated.resources.tbd_title

enum class Destinations(
    val label: StringResource,
    val title: StringResource,
    val icon: ImageVector,
    val screen: @Composable () -> Unit
) {
    Sudoku(Res.string.sudoku_label, Res.string.sudoku_title, Icons.Default.Grid3x3,
        { Text("Sudoku") }
    ),
    TBD(Res.string.tbd_label, Res.string.tbd_title, Icons.Default.QuestionMark,
        { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("TBD") } }
    ),
}

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = Destinations.valueOf(backStackEntry?.destination?.route ?: Destinations.entries.first().name)
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(currentDestination.title)) },
                )
            },
            bottomBar = {
                NavigationBar() {
                    Destinations.entries.forEach { destination ->
                        NavigationBarItem(
                            onClick = { navController.navigate(destination.name) {
                                popUpTo(navController.graph.findStartDestination().id)
                                launchSingleTop = true
                            } },
                            selected = currentDestination.name == destination.name,
                            icon = {
                                Icon(
                                    destination.icon,
                                    contentDescription = stringResource(destination.label)
                                )
                            },
                            label = { Text(stringResource(destination.label)) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Destinations.entries.first().name,
                modifier = Modifier.padding(innerPadding)
            ) {
                Destinations.entries.forEach { destination ->
                    composable(destination.name) {
                        destination.screen()
                    }
                }
            }
        }
    }
}