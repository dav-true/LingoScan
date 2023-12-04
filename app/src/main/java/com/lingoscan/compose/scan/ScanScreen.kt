@file:OptIn(ExperimentalPermissionsApi::class)

package com.lingoscan.compose.scan

import android.content.Context
import android.graphics.Bitmap
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
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.lingoscan.utils.ImageClassifierHelper
import com.lingoscan.utils.useDebounce
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun ScanScreen(
    imageClassifierHelper: ImageClassifierHelper
) {
    val showCameraView = remember { mutableStateOf(false)}
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)


    Column {
        Button(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .padding(10.dp),
            onClick = {
                if(!cameraPermissionState.status.isGranted && !cameraPermissionState.status.shouldShowRationale) {
                    cameraPermissionState.launchPermissionRequest()
                } else {
                    showCameraView.value = true
                }

            }) {
            Text(text = "Scan Image")
        }
    }

    if(showCameraView.value) {
        if (cameraPermissionState.status.isGranted) {
            ScanCameraView(imageClassifierHelper)
        } else {
            NoPermissionView(cameraPermissionState)
        }
    }
}


@Composable
fun ScanCameraView(imageClassifierHelper: ImageClassifierHelper) {
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

    resultTextDerivedState.value.useDebounce(delayMillis = 1200, delayCondition = resultTextDerivedState.value.isBlank(), onChange = {
        resultText = it
    })


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
                                Bitmap.createBitmap(imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888)
                            initializeBitmapBuffer.value = true
                        }

                        imageClassifierHelper.classify(imageProxy, bitmapBuffer, previewView.display.rotation)
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


    imageClassifierHelper.imageClassifierListener = object : ImageClassifierHelper.ClassifierListener {
        override fun onError(error: String) {
            Log.w("mytag", "onError: $error")
        }

        override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
            Log.w("mytag", "onResults: $results")
            results?.get(0)?.categories?.sortedBy { it?.index }?.filter { it.label.isNotEmpty() }?.let { results ->
                detectedResult = if (results.isNotEmpty()) {
                    results.joinToString { it?.label.toString() }
                } else {
                    ""
                }
            }
        }

    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding: PaddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                factory = {
                    previewView

                }
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {

                resultTextDerivedState.value.takeIf { it.isNotEmpty() }.let {
                    Text(
                        modifier = Modifier
                            .padding(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .wrapContentSize()
                            .background(Color.Black)
                            .padding(8.dp),
                        text = resultText,
                        color = Color.White,
                        fontSize = 22.sp
                    )
                }
            }
        }

    }
}


//private fun classifyImage(imageClassifierHelper: ImageClassifierHelper, bitmapBuffer: Bitmap, image: ImageProxy, rotation: Int) {
//    // Copy out RGB bits to the shared bitmap buffer
//    image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
//
//    // Pass Bitmap and rotation to the image classifier helper for processing and classification
//    imageClassifierHelper.classify(bitmapBuffer, rotation)
//}

@Composable
fun NoPermissionView(
    cameraPermissionState: PermissionState
) {
    Text(text = "Camera permission is not granted. Press the button below to request permission")

    Button(onClick = {
        cameraPermissionState.launchPermissionRequest()
    }) {
        Text(text = "Provide Camera Permission")
    }
}


private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}


@androidx.compose.ui.tooling.preview.Preview
@Composable
fun Preview() {
    Text(
        modifier = Modifier
            .padding(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .wrapContentSize()
            .background(Color.Black)
            .padding(8.dp)
        ,
        text = "Some text",
        color = Color.White,
        fontSize = 22.sp
    )
}
