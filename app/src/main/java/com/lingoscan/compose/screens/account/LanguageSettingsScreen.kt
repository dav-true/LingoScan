package com.lingoscan.compose.screens.account

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lingoscan.compose.components.common.LanguageDownloadingDialog
import com.lingoscan.compose.components.common.SelectLanguageButton
import com.lingoscan.compose.components.common.SelectLanguageDialog
import com.lingoscan.utils.translate.LanguageModel
import com.lingoscan.viewmodels.ComposableViewModel

@Composable fun LanguageSettingsScreen(
    navController: NavHostController
) {

    val composableViewModel = hiltViewModel<ComposableViewModel>()

    var downloadedLanguages by remember {
        mutableStateOf<List<LanguageModel>>(emptyList())
    }

    var allLanguages by remember {
        mutableStateOf<List<LanguageModel>>(emptyList())
    }

    var currentLanguageModel by remember() {
        mutableStateOf(composableViewModel.translatorHelper.getCurrentLanguage())
    }

    LaunchedEffect(Unit) {
        composableViewModel.translatorHelper.getDownloadedLanguages(onSuccess = {
            downloadedLanguages = it
        })

        composableViewModel.translatorHelper.getAllLanguages {
            allLanguages = it
        }
    }

    val showSelectLanguageDialog = remember { mutableStateOf(false) }
    val showLanguageDownloadingDialog = remember { mutableStateOf(false) }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        CurrentLanguage(languageModel = currentLanguageModel, onClick = {
            showSelectLanguageDialog.value = true
        })

        DownloadedLanguages(
            selectedLangage = currentLanguageModel,
            downloadedLanguages = downloadedLanguages, onDelete = {
                composableViewModel.translatorHelper.deleteDownloadedLanguage(it.tag, onSuccess = {
                    composableViewModel.translatorHelper.getDownloadedLanguages(onSuccess = { updatedDownloadedLanguages ->
                        downloadedLanguages = updatedDownloadedLanguages
                    })
                })
            })


        if (showSelectLanguageDialog.value) {
            SelectLanguageDialog(languages = allLanguages,
                onLanguageSelected = { selectedLanguage ->
                    // Hide selection dialog and show loading
                    showSelectLanguageDialog.value = false
                    showLanguageDownloadingDialog.value = true

                    // Download selected language
                    composableViewModel.translatorHelper.downloadLanguage(selectedLanguage.tag,
                        onSuccess = {
                            // Set current language in persistent storage on success
                            composableViewModel.persistentStorage.setTargetLanguage(selectedLanguage.tag)

                            // Update translator instance with new language
                            composableViewModel.translatorHelper.create(onSuccess = {

                                currentLanguageModel =
                                    composableViewModel.translatorHelper.getCurrentLanguage()
                                // Hide loading
                                showLanguageDownloadingDialog.value = false

                                // Update downloaded languages list
                                composableViewModel.translatorHelper.getDownloadedLanguages(
                                    onSuccess = { updatedDownloadedLanguages ->
                                        downloadedLanguages = updatedDownloadedLanguages
                                    })
                            })
                        })
                },
                onDismissRequest = {
                    showSelectLanguageDialog.value = false
                })
        }

        if (showLanguageDownloadingDialog.value) {
            LanguageDownloadingDialog()
        }
    }
}


@Composable fun CurrentLanguage(
    languageModel: LanguageModel, onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {

        Text(text = "Current selected language", style = MaterialTheme.typography.headlineSmall)

        SelectLanguageButton(language = languageModel.language, onClick = {
            onClick.invoke()
        })
    }
}


@Composable fun DownloadedLanguages(
    selectedLangage: LanguageModel,
    downloadedLanguages: List<LanguageModel>,
    onDelete: (LanguageModel) -> Unit
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 10.dp),
        text = "Downloaded languages",
        style = MaterialTheme.typography.headlineSmall
    )


    downloadedLanguages.forEach { languageModel ->
        DownloadedLanguageItem(
            languageModel = languageModel,
            isDeletable = languageModel.tag != selectedLangage.tag,
            onDelete = {
                onDelete.invoke(it)
            })
    }
}


@Composable fun DownloadedLanguageItem(
    languageModel: LanguageModel, onDelete: (LanguageModel) -> Unit, isDeletable: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 25.dp, end = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(text = languageModel.language, style = MaterialTheme.typography.bodyLarge)
        if (isDeletable) {
            IconButton(onClick = { onDelete.invoke(languageModel) }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Default.Delete),
                    tint = Color.Red,
                    contentDescription = null
                )
            }
        } else {
            Box(modifier = Modifier.size(42.dp))
        }
    }
}