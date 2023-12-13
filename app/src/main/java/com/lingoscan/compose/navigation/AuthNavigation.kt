package com.lingoscan.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lingoscan.Constants
import com.lingoscan.MainScreen
import com.lingoscan.compose.screens.auth.AuthScreen
import com.lingoscan.compose.screens.firstlaunch.FirstLaunchScreen
import com.lingoscan.utils.otherwiseFalse
import com.lingoscan.viewmodels.auth.AuthViewModel
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import io.realm.kotlin.mongodb.App


@Composable
fun AuthNavigation(
    authNavController: NavHostController,
    navController: NavHostController,
    firstLaunch: Boolean
) {

    NavHost(navController = authNavController, startDestination = getStartDestination()) {
        authRoute(navController = authNavController, firstLaunch = firstLaunch)

        composable(route = AuthNavigationRoutes.FirstLaunch) {
            FirstLaunchScreen(navController = authNavController)
        }

        composable(route = AuthNavigationRoutes.MainScreen) {
            MainScreen(navController = navController, authNavController = authNavController)
        }
    }

}

fun NavGraphBuilder.authRoute(
    navController: NavHostController,
    firstLaunch: Boolean
) {
    composable(route = AuthNavigationRoutes.AuthScreen) {
        val viewModel = hiltViewModel<AuthViewModel>()
        val authenticated by viewModel.authenticated
        val loadingState by viewModel.loadingState
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()

        AuthScreen(
            authenticated = authenticated,
            loadingState = loadingState,
            oneTapState = oneTapState,
            messageBarState = messageBarState,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            onSuccessfulSignIn = { tokenId ->
                viewModel.signInWithMongoAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess("Successfully Authenticated!")
                        viewModel.setLoading(false)
                    },
                    onError = {
                        messageBarState.addError(it)
                        viewModel.setLoading(false)
                    }
                )
            },
            onDialogDismissed = { message ->
                messageBarState.addError(Exception(message))
                viewModel.setLoading(false)
            },
            navigateToMainScreen = {
                if (firstLaunch) {
                    navController.navigate(AuthNavigationRoutes.FirstLaunch) {
                        popUpTo(AuthNavigationRoutes.AuthScreen) { inclusive = true }
                    }
                } else {
                    navController.navigate(AuthNavigationRoutes.MainScreen) {
                        popUpTo(AuthNavigationRoutes.AuthScreen) { inclusive = true }
                    }
                }
            }
        )
    }
}

object AuthNavigationRoutes {
    const val FirstLaunch = "first_launch"
    const val AuthScreen = "auth_screen"
    const val MainScreen = "main_screen"
}

fun getStartDestination(): String {
    val currentUser = App.Companion.create(Constants.ATLAS_APP_ID).currentUser
    val userLoggedIn = currentUser?.loggedIn.otherwiseFalse()

    return if (userLoggedIn) AuthNavigationRoutes.MainScreen else AuthNavigationRoutes.AuthScreen
}