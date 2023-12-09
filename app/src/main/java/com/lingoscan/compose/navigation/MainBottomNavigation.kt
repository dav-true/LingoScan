package com.lingoscan.compose.navigation

import androidx.annotation.DrawableRes

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lingoscan.R
import com.lingoscan.utils.otherwiseFalse


@Composable
fun MainBottomNavigation(
    navController: NavController
) {

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        BottomNavigationItems.entries.forEachIndexed { index, item ->
            val isSelected = currentRoute?.startsWith(item.route).otherwiseFalse()
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = null) },
                label = { Text(text = item.label) },
                selected = isSelected,
                onClick = { navController.navigate(item.route){
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                } }
            )
        }
    }
}

enum class BottomNavigationItems(
    val route: String,
    @DrawableRes val icon: Int,
    val label: String
) {
    SCAN(Routes.ScanScreen.route, R.drawable.ic_scan, "Scan"),
    LIBRARY(Routes.LibraryScreen.route, R.drawable.ic_library, "Library"),
    LEARNING(Routes.LearningScreen.route, R.drawable.ic_learning, "Learning"),
    SETTINGS(Routes.AccountScreen.route, R.drawable.ic_account, "Account")
}