package com.lingoscan.compose.navigation

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.lingoscan.compose.screens.scan.CameraScreen
import com.lingoscan.compose.screens.scan.ScanScreen
import com.lingoscan.compose.screens.scan.UploadedImageScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


@Composable
fun LingoNavigation(
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
    navController: NavHostController
) {
    navigation(startDestination = Routes.ScanScreen.Root, route = Routes.ScanScreen.route) {
        composable(Routes.ScanScreen.Root) {
            ScanScreen(navController = navController)
        }
        composable(Routes.ScanScreen.CameraView) {
            CameraScreen(navController = navController)
        }
        composable("${Routes.ScanScreen.UploadedImageScreen}/{imageUri}", arguments = listOf(
            navArgument("imageUri") {
                type = NavType.StringType
            }
        )) { entry ->
            val imageUri = URLDecoder.decode(entry.arguments?.getString("imageUri"), StandardCharsets.UTF_8.toString())

            UploadedImageScreen(navController = navController, imageUri = imageUri.toUri())
        }
    }
}

fun NavGraphBuilder.libraryScreenGraph(
    navController: NavHostController
) {
    navigation(route = Routes.LibraryScreen.route, startDestination = Routes.LibraryScreen.Root) {
        composable(Routes.LibraryScreen.Root) {
            Text(text = "Library Screen")

        }
    }
}

fun NavGraphBuilder.learningScreenGraph(
    navController: NavHostController
) {
    navigation(route = Routes.LearningScreen.route, startDestination = Routes.LearningScreen.Root) {
        composable(Routes.LearningScreen.Root) {
            Text(text = "Learning Screen")
        }
    }
}

fun NavGraphBuilder.accountScreenGraph(
    navController: NavHostController
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