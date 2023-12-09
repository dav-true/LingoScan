package com.lingoscan.compose.screens.scan

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.lingoscan.compose.scan_components.ResultViewItem
import com.lingoscan.scan.utils.ImageClassifierHelper
import com.lingoscan.scan.utils.ImageUtils
import com.lingoscan.scan.utils.getString
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
//        val bitmap = ImageUtils.getBitmap(context, imageUri)
//        bitmap?.let {
//            imageClassifierHelper.classify(it)
//        }
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
            .background(Color.White)
    ) {

        Image(
            painter = rememberAsyncImagePainter(
                model = imageUri
            ), contentDescription = "Captured Image", modifier = Modifier
                .padding(30.dp)
                .border(
                    width = 2.dp, color = Color.White, shape = RoundedCornerShape(8.dp)
                )
        )
        ResultViewItem(resultText = resultText,
            translatedText = translatedText,
            buttonText = "Add to library",
            onButtonClick = {

            })
    }
}