package com.lingoscan.compose.screens.scan

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.lingoscan.compose.components.common.CreateDictionaryDialog
import com.lingoscan.compose.components.common.SelectDictionaryDialog
import com.lingoscan.compose.components.scan.ResultViewItem
import com.lingoscan.compose.navigation.Routes
import com.lingoscan.presentations.DictionaryPresentation
import com.lingoscan.utils.scan.ImageClassifierHelper
import com.lingoscan.utils.scan.ImageUtils
import com.lingoscan.utils.scan.getString
import com.lingoscan.viewmodels.ComposableViewModel
import com.lingoscan.viewmodels.MainViewModel
import org.tensorflow.lite.task.vision.classifier.Classifications

@Composable fun UploadedImageScreen(
    navController: NavController, imageUri: Uri
) {
    val composableViewModel = hiltViewModel<ComposableViewModel>()
    val mainViewModel = hiltViewModel<MainViewModel>()
    val imageClassifierHelper = composableViewModel.imageClassifierHelper
    val translatorProvider = composableViewModel.translatorHelper

    var showSelectDictionaryDialog by remember {
        mutableStateOf(false)
    }

    var showCreateDictionaryDialog by remember {
        mutableStateOf(false)
    }

    val dictionaries by mainViewModel.dictionaries.collectAsState()

    var resultText by remember {
        mutableStateOf("")
    }

    var translatedText by remember {
        mutableStateOf("")
    }

    var resultButtonText by remember {
        mutableStateOf("Add to dictionary")
    }

    var resultButtonClick by remember {
        mutableStateOf({
            showSelectDictionaryDialog = true
        })
    }

    val context = LocalContext.current

    imageClassifierHelper.imageClassifierListener =
        object : ImageClassifierHelper.ClassifierListener {
            override fun onError(error: String) {
            }

            override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                Log.w("mytag", "onResults: $results")
                results.getString().let {
                    resultText = it
                }
            }
        }

    LaunchedEffect(Unit) {
        val bitmap = ImageUtils.getBitmap(context, imageUri)
        bitmap?.let {
            imageClassifierHelper.classify(it)
        }
    }

    LaunchedEffect(resultText) {
        resultText.takeIf { it.isNotEmpty() }?.let {
            translatorProvider.translate(resultText, onSuccess = {
                translatedText = it
            }, onFailure = {})
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = imageUri
            ), contentDescription = "Captured Image", modifier = Modifier
                .weight(1f)
                .padding(10.dp)
        )
        ResultViewItem(
            resultText = resultText,
            translatedText = translatedText,
            buttonText = resultButtonText,
            onButtonClick = resultButtonClick
        )
    }

    if (showSelectDictionaryDialog) {

        mainViewModel.getDictionaries()
        SelectDictionaryDialog(dictionaries = dictionaries.orEmpty(), onDismissRequest = {
            showSelectDictionaryDialog = false
        }, onSelectDictionary = {
            mainViewModel.addWordToDictionary(
                dictionaryId = it.id,
                name = resultText,
                translation = translatedText,
                image = ImageUtils.getBase64FromPath(imageUri.toString())
            )
            resultButtonText = "Go to library"
            resultButtonClick = {
                navController.navigate(Routes.LibraryScreen.Root) {
                    popUpTo(Routes.ScanScreen.Root) {
                        inclusive = true
                    }
                }
            }

            Toast.makeText(context, "Word was added to dictionary!", Toast.LENGTH_LONG).show()
        }, onCreateDictionary = {
            showSelectDictionaryDialog = false
            showCreateDictionaryDialog = true
        })
    }

    if (showCreateDictionaryDialog) {
        CreateDictionaryDialog(onDismissRequest = {
            showCreateDictionaryDialog = false
        }, onCreateDictionary = { dictionaryName ->
            mainViewModel.createDictionaryAndAddWord(
                dictionaryName = dictionaryName,
                wordName = resultText,
                translation = translatedText,
                image = ImageUtils.getBase64FromPath(imageUri.toString())
            )
        })
    }
}


@Preview
@Composable fun UploadedImageScreenPreview() {
    UploadedImageScreen(navController = NavController(LocalContext.current), imageUri = Uri.EMPTY)
}