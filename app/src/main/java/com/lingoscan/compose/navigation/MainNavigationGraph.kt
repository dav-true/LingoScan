package com.lingoscan.compose.navigation

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation


@Composable
fun MainNavigationGraph(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Routes.ScanScreen.route) {
        scanScreenGraph(navController = navController)
        libraryScreenGraph(navController = navController)
        learningScreenGraph(navController = navController)
        accountScreenGraph(navController = navController)
    }
}


fun NavGraphBuilder.scanScreenGraph(
    navController: NavController
) {
    navigation(startDestination = Routes.ScanScreen.Root, route = Routes.ScanScreen.route) {
        composable(Routes.ScanScreen.Root) {
            Text(text = "Scan Screen")
            Button(onClick = { navController.navigate(Routes.ScanScreen.CameraView) }) {

            }
        }
        composable(Routes.ScanScreen.CameraView) {
            Text(text = "Camera View Screen")
        }
        composable(Routes.ScanScreen.UploadedImageScreen) {

        }
    }
}

fun NavGraphBuilder.libraryScreenGraph(
    navController: NavController
) {
    navigation(route = Routes.LibraryScreen.route, startDestination = Routes.LibraryScreen.Root) {
        composable(Routes.LibraryScreen.Root) {
            Text(text = "Library Screen")

        }
    }
}

fun NavGraphBuilder.learningScreenGraph(
    navController: NavController
) {
    navigation(route = Routes.LearningScreen.route, startDestination = Routes.LearningScreen.Root) {
        composable(Routes.LearningScreen.Root) {
            Text(text = "Learning Screen")
        }
    }
}

fun NavGraphBuilder.accountScreenGraph(
    navController: NavController
) {
    navigation(route = Routes.AccountScreen.route, startDestination = Routes.AccountScreen.Root) {
        composable(Routes.AccountScreen.Root) {
            Text(text = "Account Screen")
        }
    }
}


sealed class Routes {
    object ScanScreen : Routes() {
        const val route = "scan"
        const val Root = "scan/screen"
        const val CameraView = "scan/sreen/camera_view"
        const val UploadedImageScreen = "scan/screen/uploaded_image_screen"
    }

    object LibraryScreen : Routes() {
        const val route = "library"
        const val Root = "library/library"
    }

    object LearningScreen : Routes() {
        const val route = "learning"
        const val Root = "learning/screen"
    }

    object AccountScreen : Routes() {
        const val route = "account"
        const val Root = "account/screen"
    }
}