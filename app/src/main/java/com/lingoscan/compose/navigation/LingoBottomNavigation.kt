package com.lingoscan.compose.navigation

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lingoscan.R
import com.lingoscan.utils.CountryFlags
import com.lingoscan.utils.otherwiseFalse
import com.lingoscan.viewmodels.ComposableViewModel
import kotlin.enums.EnumEntries


@Composable
fun LingoBottomNavigation(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val composableViewModel = hiltViewModel<ComposableViewModel>()

    val isBottomBarVisible = BottomNavigationItems.entries.rootScreens().contains(currentRoute).otherwiseFalse()

    AnimatedVisibility(visible = isBottomBarVisible) {
        NavigationBar {
            BottomNavigationItems.entries.forEachIndexed { index, item ->
                val isSelected = currentRoute == item.rootScreen
                NavigationBarItem(
                    icon = {
                        if (item == BottomNavigationItems.SETTINGS) {
                            Text(text = CountryFlags.getCountryFlagByCountryCode(composableViewModel.persistentStorage.targetLanguage))
                        } else {
                            Icon(painterResource(id = item.icon), contentDescription = null)
                        }
                    },
                    label = { Text(text = item.label) },
                    selected = isSelected,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

enum class BottomNavigationItems(
    val route: String,
    val rootScreen: String,
    @DrawableRes val icon: Int,
    val label: String
) {
    SCAN(Routes.ScanScreen.route, Routes.ScanScreen.Root, R.drawable.ic_scan, "Scan"),
    LIBRARY(Routes.LibraryScreen.route, Routes.LibraryScreen.Root, R.drawable.ic_library, "Library"),
    LEARNING(Routes.LearningScreen.route, Routes.LearningScreen.Root, R.drawable.ic_learning, "Learning"),
    SETTINGS(Routes.AccountScreen.route, Routes.AccountScreen.Root, R.drawable.ic_account, "Account")
}

fun EnumEntries<BottomNavigationItems>.rootScreens() : List<String> {
    return BottomNavigationItems.entries.map { it.rootScreen }
}