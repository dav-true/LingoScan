package com.lingoscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.lingoscan.compose.navigation.LingoBottomNavigation
import com.lingoscan.compose.navigation.LingoNavigation
import com.lingoscan.compose.navigation.LingoToolbar
//import com.lingoscan.compose.screens.ScanScreen
import com.lingoscan.ui.theme.LingoScanTheme
import com.lingoscan.scan.utils.ImageClassifierHelper
import com.lingoscan.translate.utils.TranslatorProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var imageClassifierHelper: ImageClassifierHelper
    @Inject
    lateinit var translatorProvider: TranslatorProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val navController = rememberNavController()
            LingoScanTheme {
                // A surface container using the 'background' color from the theme
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
        }

//        translatorProvider.setTargetLanguage(TranslateLanguage.UKRAINIAN)
//
//        translatorProvider.create(
//            onStart = {
//                Log.w("mytag", "model downloading started")
//            },
//            onSuccess = {
//                Log.w("mytag", "model downloaded")
//
//            },
//            onFailure = {
//                Log.w("mytag", it.message.toString())
//
//            })
//
//        setContent {
//            LingoScanTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    ScanScreen(
//                        imageClassifierHelper = imageClassifierHelper,
//                        translatorProvider = translatorProvider
//                    )
//                }
//            }
//        }
    }
}