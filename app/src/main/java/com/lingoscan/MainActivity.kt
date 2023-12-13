package com.lingoscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lingoscan.compose.components.common.LanguageDownloadingDialog
import com.lingoscan.compose.components.common.TextFieldDialog
import com.lingoscan.compose.navigation.AuthNavigation
import com.lingoscan.compose.navigation.LingoBottomNavigation
import com.lingoscan.compose.navigation.LingoNavigation
import com.lingoscan.compose.navigation.LingoToolbar
//import com.lingoscan.compose.screens.ScanScreen
import com.lingoscan.ui.theme.LingoScanTheme
import com.lingoscan.utils.preferences.PersistentStorage
import com.lingoscan.utils.scan.ImageClassifierHelper
import com.lingoscan.utils.translate.TranslatorHelper
import com.lingoscan.viewmodels.ComposableViewModel
import com.lingoscan.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var imageClassifierHelper: ImageClassifierHelper

    @Inject
    lateinit var translatorHelper: TranslatorHelper

    @Inject
    lateinit var persistentStorage: PersistentStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val firstLaunchController = rememberNavController()
            val navController = rememberNavController()

            LingoScanTheme {
                AuthNavigation(
                    authNavController = firstLaunchController,
                    navController = navController,
                    firstLaunch = persistentStorage.isFirstLaunch
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    authNavController: NavHostController
) {
    var showCreateDictionaryDialog by remember {
        mutableStateOf(false)
    }

    var showLanguageLoadingDialog by remember {
        mutableStateOf(true)
    }

    val mainViewModel = hiltViewModel<MainViewModel>()
    val composableViewModel = hiltViewModel<ComposableViewModel>()

    LaunchedEffect(Unit) {
        composableViewModel.translatorHelper.create(onSuccess = {
            showLanguageLoadingDialog = false
        })
    }
    Scaffold(
        topBar = {
            LingoToolbar(
                navController = navController,
                onCreateDictionary = {
                    showCreateDictionaryDialog = true
                })
        },
        bottomBar = {
            LingoBottomNavigation(navController = navController)
        }
    ) {
        Surface(modifier = Modifier.padding(it)) {
            LingoNavigation(navController = navController, authNavController = authNavController)
        }
    }

    if (showCreateDictionaryDialog) {
        TextFieldDialog(
            title = "Create dictionary",
            textFieldPlaceholder = "Dictionary name",
            confirmButtonText = "Create",
            onDismissRequest = {
                showCreateDictionaryDialog = false
            },
            onConfirm = {
                mainViewModel.createDictionary(name = it)
                showCreateDictionaryDialog = false
            }
        )
    }

    if(showLanguageLoadingDialog) {
        LanguageDownloadingDialog()
    }
}