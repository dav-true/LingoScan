package com.lingoscan.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lingoscan.MainScreen
import com.lingoscan.compose.screens.firstlaunch.FirstLaunchScreen


@Composable
fun FirstLaunchNavigation(
    firstLaunchController: NavHostController,
    navController: NavHostController,
    firstLaunch: Boolean
) {

    val startDestination = if (firstLaunch) FirstLaunchNavigationRoutes.FirstLaunch else FirstLaunchNavigationRoutes.MainScreen

    NavHost(navController = firstLaunchController, startDestination =  startDestination) {
        composable(route = FirstLaunchNavigationRoutes.FirstLaunch) {
            FirstLaunchScreen(navController = firstLaunchController)
        }

        composable(route = FirstLaunchNavigationRoutes.MainScreen) {
            MainScreen(navController = navController)
        }
    }

}

object FirstLaunchNavigationRoutes {
    const val FirstLaunch = "first_launch"
    const val MainScreen = "main_screen"
}