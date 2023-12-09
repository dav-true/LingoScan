package com.lingoscan.compose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lingoscan.ui.theme.Purple80
import com.lingoscan.ui.theme.PurpleGrey80
import com.lingoscan.ui.theme.ToolbarBackgroundColor
import com.lingoscan.ui.theme.onBackgroundHighlighted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LingoToolbar(
    navController: NavHostController,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRootScreen = currentBackStackEntry?.destination?.parent?.route
    val currentDestination = currentBackStackEntry?.destination?.route

    val rootScreen = BottomNavigationItems.entries.map { it.rootScreen }.contains(currentDestination)

    val title = currentRootScreen?.capitalize(Locale.current)

    TopAppBar(
        title = { Text(text = title.orEmpty(), color = onBackgroundHighlighted) },
        navigationIcon = {
            if (!rootScreen) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Default.ArrowBack),
                        contentDescription = "ArrowBack"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = ToolbarBackgroundColor),
        actions = { }
    )
}

