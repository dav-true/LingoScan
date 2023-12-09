package com.lingoscan.compose.screens.scan

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.lingoscan.compose.components.scan.ResultViewItem
import com.lingoscan.utils.scan.ImageClassifierHelper
import com.lingoscan.utils.scan.ImageUtils
import com.lingoscan.utils.scan.getString
import com.lingoscan.viewmodels.ComposableViewModel
import org.tensorflow.lite.task.vision.classifier.Classifications

@Composable fun UploadedImageScreen(
    navController: NavController, imageUri: Uri
) {

    val composableViewModel = hiltViewModel<ComposableViewModel>()
    val imageClassifierHelper = composableViewModel.imageClassifierHelper
    val translatorProvider = composableViewModel.translatorHelper

    var resultText by remember {
        mutableStateOf("")
    }

    var translatedText by remember {
        mutableStateOf("")
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
            ),
            contentDescription = "Captured Image",
            modifier = Modifier
                .weight(1f)
                .padding(10.dp)
        )
        ResultViewItem(
            resultText = resultText,
            translatedText = translatedText,
            buttonText = "Add to library",
            onButtonClick = {
            })
    }
}

@Preview
@Composable
fun UploadedImageScreenPreview() {
    UploadedImageScreen(navController = NavController(LocalContext.current), imageUri = Uri.EMPTY)
}