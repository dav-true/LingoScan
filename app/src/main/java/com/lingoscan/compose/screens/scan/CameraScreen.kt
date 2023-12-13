@file:Suppress("DEPRECATION")

package com.lingoscan.compose.screens.scan

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.Surface.ROTATION_0
import android.view.Surface.ROTATION_90
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.lingoscan.compose.components.scan.ResultViewItem
import com.lingoscan.compose.components.scan.ScannerFrame
import com.lingoscan.compose.navigation.Routes
import com.lingoscan.utils.scan.ImageClassifierHelper
import com.lingoscan.utils.scan.ImageUtils
import com.lingoscan.utils.scan.getString
import com.lingoscan.utils.getCameraProvider
import com.lingoscan.utils.useDebounce
import com.lingoscan.viewmodels.ComposableViewModel
import kotlinx.coroutines.delay
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors


@Composable fun CameraScreen(
    navController: NavHostController
) {
    val composableViewModel = hiltViewModel<ComposableViewModel>()
    val imageClassifierHelper = composableViewModel.imageClassifierHelper
    val translatorHelper = composableViewModel.translatorHelper

    val initializeBitmapBuffer = remember { mutableStateOf(false) }
    var bitmapBuffer = remember { Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) }

    //Camera surface properties
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val executor = remember { Executors.newSingleThreadExecutor() }

    val lensFacing = CameraSelector.LENS_FACING_BACK
    val preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    //endregion

    // Result states
    var detectedResult by remember() {
        mutableStateOf("")
    }

    val resultTextDerivedState = remember {
        derivedStateOf {
            detectedResult
        }
    }

    var resultText by remember {
        mutableStateOf("")
    }

    resultTextDerivedState.value.useDebounce(delayMillis = 1000,
        delayCondition = resultTextDerivedState.value.isBlank(),
        onChange = {
            resultText = it
        })

    var translatedText by remember {
        mutableStateOf("")
    }

    var capturedImageUri by remember {
        mutableStateOf(Uri.EMPTY)
    }

    LaunchedEffect(resultText) {
        translatorHelper.translate(resultText, onSuccess = {
            translatedText = it
        }, onFailure = {
            Log.w("mytag", it.message.toString())
        })
    }
    //endregion


    //Camera setup
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()

        val imageAnalyzer = ImageAnalysis.Builder().setTargetRotation(ROTATION_0)
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setOutputImageRotationEnabled(true)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888).build().also {
                it.setAnalyzer(executor) { imageProxy ->
                    if (!initializeBitmapBuffer.value) {
                        bitmapBuffer = Bitmap.createBitmap(
                            imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
                        )
                        initializeBitmapBuffer.value = true
                    }

                    imageClassifierHelper.classify(
                        imageProxy, bitmapBuffer, ROTATION_90
                    )
                }
            }


        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview, imageCapture, imageAnalyzer
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }
    //endregion


    // Image classifier listener setup
    imageClassifierHelper.imageClassifierListener =
        object : ImageClassifierHelper.ClassifierListener {
            override fun onError(error: String) {
                Log.w("mytag", "onError: $error")
            }

            override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                Log.w("mytag", "onResults: ${results}, inferenceTime: ${inferenceTime}")
                results.getString().let {
                    detectedResult = it
                }
            }
        }
    //endregion

    LaunchedEffect(capturedImageUri) {
        capturedImageUri.takeIf { it.toString().isNotBlank() }?.let {
            executor.shutdownNow()
            val encodedUri = URLEncoder.encode(it.toString(), StandardCharsets.UTF_8.toString())
            navController.navigate("${Routes.ScanScreen.UploadedImageScreen}/${encodedUri}")
        }
    }
    // Camera preview + camera surface + result view
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding: PaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AndroidView(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), factory = {
                previewView

            })
            CameraSurface(resultText = resultText, translatedText = translatedText, onCapture = {
                ImageUtils.takePhoto(imageCapture = imageCapture,
                    outputDirectory = context.filesDir,
                    executor = executor,
                    onImageCaptured = { uri ->
                        capturedImageUri = uri
                    })
            }, onBackPress = {
                executor.shutdownNow()
                navController.popBackStack()
            })

        }
    }
    //endregion
}

@Composable fun CameraSurface(
    resultText: String, translatedText: String, onCapture: () -> Unit, onBackPress: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScannerFrame(
                modifier = Modifier
                    .padding(bottom = 130.dp)
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.6f)
            )
        }

        resultText.takeIf { it.isNotBlank() }?.let {
            ResultViewItem(
                modifier = Modifier.fillMaxSize(),
                resultText = resultText,
                translatedText = translatedText,
                buttonText = "Capture",
                onButtonClick = {
                    onCapture.invoke()
                })
        }
    }
}

