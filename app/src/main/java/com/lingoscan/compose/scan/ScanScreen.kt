@file:OptIn(ExperimentalPermissionsApi::class)

package com.lingoscan.compose.scan

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.lingoscan.scan.utils.ImageClassifierHelper
import com.lingoscan.scan.utils.ImageUtils
import com.lingoscan.scan.utils.getString
import com.lingoscan.translate.utils.TranslatorProvider
import com.lingoscan.utils.getCameraProvider
import com.lingoscan.utils.useDebounce
import kotlinx.coroutines.delay
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.util.concurrent.Executors

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun ScanScreen(
    imageClassifierHelper: ImageClassifierHelper,
    translatorProvider: TranslatorProvider
) {
    val showCameraView = remember { mutableStateOf(false) }
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    Column {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            onClick = {
                if (!cameraPermissionState.status.isGranted) {
                    cameraPermissionState.launchPermissionRequest()
                } else {
                    showCameraView.value = true
                }

            }) {
            Text(text = "Scan Image", fontSize = 20.sp)
        }
    }

    if (showCameraView.value) {
        CameraView(imageClassifierHelper, translatorProvider, onBackPress = {
            showCameraView.value = false
        })
    }
}


@Composable
fun CameraView(
    imageClassifierHelper: ImageClassifierHelper,
    translatorProvider: TranslatorProvider,
    onBackPress: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val executor = remember { Executors.newSingleThreadExecutor() }

    val initializeBitmapBuffer = remember { mutableStateOf(false) }
    var bitmapBuffer = remember { Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) }

    val lensFacing = CameraSelector.LENS_FACING_BACK

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

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

    resultTextDerivedState.value.useDebounce(
        delayMillis = 1200,
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
        translatorProvider.translate(resultText, onSuccess = {
            translatedText = it
        }, onFailure = {
            Log.w("mytag", it.message.toString())
        })
    }

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()

        val imageAnalyzer =
            ImageAnalysis.Builder()
                .setTargetRotation(previewView.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also {
                    it.setAnalyzer(executor) { imageProxy ->
                        if (!initializeBitmapBuffer.value) {
                            bitmapBuffer =
                                Bitmap.createBitmap(
                                    imageProxy.width,
                                    imageProxy.height,
                                    Bitmap.Config.ARGB_8888
                                )
                            initializeBitmapBuffer.value = true
                        }

                        imageClassifierHelper.classify(
                            imageProxy,
                            bitmapBuffer,
                            previewView.display.rotation
                        )
                    }
                }

        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture,
            imageAnalyzer
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }


    imageClassifierHelper.imageClassifierListener =
        object : ImageClassifierHelper.ClassifierListener {
            override fun onError(error: String) {
                Log.w("mytag", "onError: $error")
            }

            override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                Log.w("mytag", "onResults: $results")
                results.getString().let {
                    detectedResult = it
                }
            }
        }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding: PaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                factory = {
                    previewView

                }
            )
            CameraSurface(
                resultText = resultText,
                translatedText = translatedText,
                onCapture = {
                    ImageUtils.takePhoto(
                        imageCapture = imageCapture,
                        outputDirectory = context.filesDir,
                        executor = executor,
                        onImageCaptured = { uri ->
                            capturedImageUri = uri
                        })
                },
                onBackPress = {
                    executor.shutdownNow()
                    onBackPress.invoke()
                })


            AnimatedVisibility(
                enter = scaleIn(
                    initialScale = 0.5f
                ),
                exit = fadeOut(),
                visible = capturedImageUri != Uri.EMPTY) {
                Image(
                    painter = rememberImagePainter(
                        data = capturedImageUri
                    ),
                    contentDescription = "Captured Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp)
                        .border(
                            width = 2.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp)
                        )
                )

                LaunchedEffect(key1 = Unit) {
                    delay(3000)
                    capturedImageUri = Uri.EMPTY
                }
            }

        }

    }
}

@Composable
fun CameraSurface(
    resultText: String,
    translatedText: String,
    onCapture: () -> Unit,
    onBackPress: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Row {
            IconButton(onClick = onBackPress) {
                Icon(
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ).padding(5.dp),
                    painter = rememberVectorPainter(image = Icons.Default.ArrowBack),
                    tint = Color.White,
                    contentDescription = "BackButton"
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            ScannerFrame(
                modifier = Modifier
                    .padding(bottom = 100.dp)
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.5f)
            )
        }

        resultText.takeIf { it.isNotBlank() }?.let {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(20.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .wrapContentSize()
                                .background(Color.Black)
                                .padding(8.dp),
                            text = translatedText,
                            color = Color.White,
                            fontSize = 22.sp
                        )

                        Text(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .wrapContentSize()
                                .padding(8.dp),
                            text = resultText,
                            color = Color.Black,
                            fontSize = 18.sp
                        )
                    }
                }

                Button(
                    modifier = Modifier.padding(bottom = 10.dp),
                    onClick = onCapture) {
                    Text(text = "Capture!", fontSize = 22.sp)
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun Preview() {
    CameraSurface(resultText = "More", translatedText = "Some", onCapture = { }, onBackPress = { })
}