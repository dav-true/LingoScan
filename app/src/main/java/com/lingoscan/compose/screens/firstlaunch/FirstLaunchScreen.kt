package com.lingoscan.compose.screens.firstlaunch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lingoscan.compose.components.common.LanguageDownloadingDialog
import com.lingoscan.compose.components.common.SelectLanguageButton
import com.lingoscan.compose.components.common.SelectLanguageDialog
import com.lingoscan.compose.navigation.AuthNavigationRoutes
import com.lingoscan.ui.theme.PurpleLight
import com.lingoscan.utils.translate.LanguageModel
import com.lingoscan.viewmodels.ComposableViewModel

@Composable
fun FirstLaunchScreen(
    navController: NavHostController
) {
    val composableViewModel = hiltViewModel<ComposableViewModel>()

    var allLanguages by remember {
        mutableStateOf<List<LanguageModel>>(emptyList())
    }

    LaunchedEffect(Unit) {
        composableViewModel.translatorHelper.getAllLanguages {
            allLanguages = it
        }
    }

    val showSelectLanguageDialog = remember { mutableStateOf(false) }
    val showLanguageDownloadingDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(PurpleLight)
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp),
    ) {
        Text(
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            text = "Welcome to the LingoScan app!",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This app is designed to help you learn new words in a foreign language by scanning objects around you.",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Please, select the language you want to learn",
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(modifier = Modifier.height(16.dp))

        SelectLanguageButton(language = "Select language") {
            // Show dialog with all languages
            showSelectLanguageDialog.value = true
        }
    }

    if (showSelectLanguageDialog.value) {
        SelectLanguageDialog(
            languages = allLanguages,
            onLanguageSelected = { languageModel ->
                showSelectLanguageDialog.value = false
                showLanguageDownloadingDialog.value = true
                composableViewModel.translatorHelper.downloadLanguage(
                    languageModel.tag,
                    onSuccess = {
                        composableViewModel.persistentStorage.setTargetLanguage(languageModel.tag)

                        composableViewModel.translatorHelper.create(onSuccess = {
                            showLanguageDownloadingDialog.value = false
                            navController.navigate(AuthNavigationRoutes.MainScreen) {
                                popUpTo(AuthNavigationRoutes.FirstLaunch) {
                                    inclusive = true
                                }
                            }
                        })
                    })

            },
            onDismissRequest = {
                showSelectLanguageDialog.value = false
            }
        )
    }

    if (showLanguageDownloadingDialog.value) {
        LanguageDownloadingDialog()
    }
}

@Preview
@Composable
fun FirstLaunchScreenPreview() {

    Column(
        modifier = Modifier
            .background(PurpleLight)
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp),
    ) {
        Text(
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            text = "Welcome to the LingoScan app!",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This app is designed to help you learn new words in a foreign language by scanning objects around you.",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Please, select the language you want to learn",
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(modifier = Modifier.height(16.dp))

        SelectLanguageButton(language = "Select language") {
            // Show dialog with all languages
//            showSelectLanguageDialog.value = true
        }
    }
}

