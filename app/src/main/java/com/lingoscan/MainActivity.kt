package com.lingoscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lingoscan.compose.navigation.FirstLaunchNavigation
import com.lingoscan.compose.navigation.LingoBottomNavigation
import com.lingoscan.compose.navigation.LingoNavigation
import com.lingoscan.compose.navigation.LingoToolbar
//import com.lingoscan.compose.screens.ScanScreen
import com.lingoscan.ui.theme.LingoScanTheme
import com.lingoscan.utils.preferences.PersistentStorage
import com.lingoscan.utils.scan.ImageClassifierHelper
import com.lingoscan.utils.translate.TranslatorHelper
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
                // A surface container using the 'background' color from the theme
                FirstLaunchNavigation(
                    firstLaunchController = firstLaunchController,
                    navController = navController,
                    firstLaunch = persistentStorage.isFirstLaunch
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            LingoToolbar(navController = navController)
        },
        bottomBar = {
            LingoBottomNavigation(navController = navController)
        }
    ) {
        Surface(modifier = Modifier.padding(it)) {
            LingoNavigation(navController = navController)
        }
    }
}