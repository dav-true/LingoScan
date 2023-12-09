@file:OptIn(ExperimentalPermissionsApi::class)

package com.lingoscan.compose.screens.scan

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.lingoscan.compose.scan.CameraView
import com.lingoscan.scan.utils.ImageClassifierHelper
import com.lingoscan.scan.utils.ImageUtils
import com.lingoscan.scan.utils.getString
import com.lingoscan.translate.utils.TranslatorProvider
import org.tensorflow.lite.task.vision.classifier.Classifications



//@Composable @OptIn(ExperimentalPermissionsApi::class) fun ScanScreen(
//    imageClassifierHelper: ImageClassifierHelper, translatorProvider: TranslatorProvider
//) {
//    val showCameraView = remember { mutableStateOf(false) }
//    val showUploadedImage = remember { mutableStateOf(false) }
//
//    val uploadedImageUri = remember { mutableStateOf(Uri.EMPTY) }
////    val uploadedImageResultText = remember { mutableStateOf("") }
////    val uploadedImageTranslatedText = remember { mutableStateOf("") }
//
//    var classifiedText by remember { mutableStateOf("") }
//    var translatedText by remember { mutableStateOf("") }
//
//    imageClassifierHelper.imageClassifierListener =
//        object : ImageClassifierHelper.ClassifierListener {
//            override fun onError(error: String) {
//            }
//
//            override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
//                results.getString().let {
//                    classifiedText = it
//                }
//            }
//        }
//
//    translatorProvider.translate(classifiedText, onSuccess = {
//        translatedText = it
//    }, onFailure = {})
//
//
//    Column {
//        //Scanner button view
//        ScannerButton(onShowCameraView = {
//            showCameraView.value = true
//        })
//
//        ImagePickerButton(imageClassifierHelper = imageClassifierHelper,
//            translatorProvider = translatorProvider,
//            onShowScannedImage = { imageUri, resultText, translatedText ->
//                uploadedImageUri.value = imageUri
////                uploadedImageResultText.value = resultText
////                uploadedImageTranslatedText.value = translatedText
////                showUploadedImage.value = true
//            })
//    }
//
//    // Scanner camera view
//    if (showCameraView.value) {
//        CameraView(
//            imageClassifierHelper = imageClassifierHelper,
//            translatorProvider = translatorProvider,
//            onBackPress = {
//                showCameraView.value = false
//            })
//    }
//
//    AnimatedVisibility(visible = showUploadedImage.value, enter = scaleIn(), exit = fadeOut()) {
////        UploadedImageScreen(
////            uploadedImageUri = uploadedImageUri.value,
////            uploadedImageResultText = uploadedImageResultText.value,
////            uploadedImageTranslatedText = uploadedImageTranslatedText.value,
////            onBackPressed = { showUploadedImage.value = false }
////        )
//    }
//}
//
//@Composable fun ImagePickerButton(
//    imageClassifierHelper: ImageClassifierHelper,
//    translatorProvider: TranslatorProvider,
//    onShowScannedImage: (imageUri: Uri, resultText: String, translatedText: String) -> Unit
//) {
//    var imageUri by remember { mutableStateOf(Uri.EMPTY) }
//    var bitmapBuffer by remember { mutableStateOf<Bitmap?>(null) }
//
////    var resultText by remember {
////        mutableStateOf("")
////    }
//
//    val context = LocalContext.current
//
////    imageClassifierHelper.imageClassifierListener =
////        object : ImageClassifierHelper.ClassifierListener {
////            override fun onError(error: String) {
////                Log.w("mytag", "onError: $error")
////            }
////
////            override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
////                Log.w("mytag", "onResults: $results")
////                results.getString().let {
////                    resultText = it
////                }
////            }
////        }
//
////    LaunchedEffect(resultText) {
////        resultText.takeIf { it.isNotEmpty() }?.let {
////            translatorProvider.translate(resultText, onSuccess = { translatedText ->
////                onShowScannedImage.invoke(imageUri, resultText, translatedText)
////            }, onFailure = {})
////        }
////    }
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let {
//            imageUri = uri
//            bitmapBuffer = ImageUtils.getBitmap(context, uri)
//            bitmapBuffer?.let {
//                imageClassifierHelper.classify(it)
//            }
//        }
//    }
//
//    Button(modifier = Modifier
//        .fillMaxWidth()
//        .padding(10.dp), onClick = {
//        launcher.launch("image/*")
//    }) {
//        Text(text = "Pick Image", fontSize = 20.sp)
//    }
//}
//
//@Composable fun ScannerButton(
//    onShowCameraView: () -> Unit
//) {
//    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
//
//    Button(modifier = Modifier
//        .fillMaxWidth()
//        .padding(10.dp), onClick = {
//        if (!cameraPermissionState.status.isGranted) {
//            cameraPermissionState.launchPermissionRequest()
//        } else {
//            onShowCameraView.invoke()
//        }
//
//    }) {
//        Text(text = "Scan Image", fontSize = 20.sp)
//    }
//}