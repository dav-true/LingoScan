package com.lingoscan.compose.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.lingoscan.compose.navigation.Routes.LearningScreen.LearningResultsScreen
import com.lingoscan.compose.screens.account.SettingsScreen
import com.lingoscan.compose.screens.account.DevSettingsScreen
import com.lingoscan.compose.screens.account.LanguageSettingsScreen
import com.lingoscan.compose.screens.learning.LearningResultsScreen
import com.lingoscan.compose.screens.learning.LearningScreen
import com.lingoscan.compose.screens.learning.LearningWordSelectionScreen
import com.lingoscan.compose.screens.library.DictionaryScreen
import com.lingoscan.compose.screens.library.LibraryScreen
import com.lingoscan.compose.screens.library.WordScreen
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
        settingsScreenGraph(navController = navController)
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
            val imageUri = URLDecoder.decode(
                entry.arguments?.getString("imageUri"),
                StandardCharsets.UTF_8.toString()
            )
            UploadedImageScreen(navController = navController, imageUri = imageUri.toUri())
        }
    }
}

fun NavGraphBuilder.libraryScreenGraph(
    navController: NavHostController
) {
    navigation(route = Routes.LibraryScreen.route, startDestination = Routes.LibraryScreen.Root) {
        composable(Routes.LibraryScreen.Root) {
            LibraryScreen(navController = navController)
        }

        composable(route = "${Routes.LibraryScreen.Dictionary}/{dictionaryId}", arguments = listOf(
            navArgument("dictionaryId") {
                type = NavType.StringType
            }
        )) { entry ->
            val dictionaryId = entry.arguments?.getString("dictionaryId").orEmpty()
            Log.d("DictionaryId", dictionaryId)
            DictionaryScreen(navController = navController, dictionaryId = dictionaryId)
        }

        composable(route = "${Routes.LibraryScreen.Word}/{wordId}", arguments = listOf(
            navArgument("wordId") {
                type = NavType.StringType
            }
        )) { entry ->
            val wordId = entry.arguments?.getString("wordId").orEmpty()
            WordScreen(navController = navController, wordId = wordId)
        }
    }
}

fun NavGraphBuilder.learningScreenGraph(
    navController: NavHostController
) {
    navigation(route = Routes.LearningScreen.route, startDestination = Routes.LearningScreen.Root) {
        composable(Routes.LearningScreen.Root) {
            LearningScreen(navController = navController)
        }

        composable(
            route = "${Routes.LearningScreen.WordSelection}/{dictionaryId}", arguments = listOf(
                navArgument("dictionaryId") {
                    type = NavType.StringType
                })
        ) { entry ->

            val dictionaryId = entry.arguments?.getString("dictionaryId").orEmpty()
            LearningWordSelectionScreen(navController = navController, dictionaryId = dictionaryId)
        }

        composable(route = Routes.LearningScreen.LearningResultsScreen) {
            LearningResultsScreen(navController)
        }
    }
}

fun NavGraphBuilder.settingsScreenGraph(
    navController: NavHostController
) {
    navigation(route = Routes.Settings.route, startDestination = Routes.Settings.Root) {
        composable(Routes.Settings.Root) {
            SettingsScreen(navController = navController)
        }
        composable(Routes.Settings.DevSettings) {
            DevSettingsScreen(navController = navController)
        }

        composable(Routes.Settings.LanguageSettings) {
            LanguageSettingsScreen(navController = navController)
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
        const val Dictionary = "library/library/dictionary"
        const val Word = "library/library/dictionary/word"
    }

    object LearningScreen : Routes() {
        const val route = "learning"
        const val Root = "learning/screen"
        const val WordSelection = "learning/screen/word_selection"
        const val LearningResultsScreen = "learning/screen/learning_results_screen"
    }

    object Settings : Routes() {
        const val route = "settings"
        const val Root = "settings/screen"
        const val DevSettings = "account/screen/dev_settings"
        const val LanguageSettings = "account/screen/language_settings"
    }
}